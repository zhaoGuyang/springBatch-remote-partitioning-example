package com.kailing.bootbatch.partitionjob.config;

import com.kailing.bootbatch.partitionjob.Item.MyProcessorItem;
import com.kailing.bootbatch.partitionjob.Item.PrintWriterItem;
import com.kailing.bootbatch.partitionjob.entity.Article;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kl on 2018/3/1.
 * Content :Slave从节点，真实业务处理
 */
@Configuration
public class SlaveConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    //主要负责从JobRepository中获取执行的信息，包括获取作业实例、获取作业执行器、获取作恶步执行器、获取正在运行的作业执行器、获取作业列表等操作；
    @Autowired
    public JobExplorer jobExplorer;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    EntityManagerFactory entityManagerFactory;
    //从节点接受信息并处理
    @Bean
    @Profile({"slave","mixed"})
    @ServiceActivator(inputChannel = "inboundRequests", outputChannel = "outboundStaging")
    public StepExecutionRequestHandler stepExecutionRequestHandler() {
        StepExecutionRequestHandler stepExecutionRequestHandler = new StepExecutionRequestHandler();
        BeanFactoryStepLocator stepLocator = new BeanFactoryStepLocator();
        stepLocator.setBeanFactory(this.applicationContext);
        stepExecutionRequestHandler.setStepLocator(stepLocator);
        stepExecutionRequestHandler.setJobExplorer(this.jobExplorer);
        return stepExecutionRequestHandler;
    }



    @Bean("slaveStep")
    public Step slaveStep(MyProcessorItem processorItem,
                          JpaPagingItemReader reader) {
        //调用多个 ItemProcessor
        CompositeItemProcessor itemProcessor = new CompositeItemProcessor();
        List<ItemProcessor> processorList = new ArrayList<>();
        processorList.add(processorItem);
        itemProcessor.setDelegates(processorList);
        return stepBuilderFactory.get("slaveStep")
                .<Article, Article>chunk(1)//事务提交批次
                .reader(reader)
                .processor(itemProcessor)
                .writer(new PrintWriterItem())
                .build();
    }
    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<Article> jpaPagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue) {
        System.err.println("接收到分片参数["+minValue+"->"+maxValue+"]");
        JpaPagingItemReader<Article> reader = new JpaPagingItemReader<>();
        JpaNativeQueryProvider queryProvider = new JpaNativeQueryProvider<>();
        String sql = "select * from kl_article where  arcid >= :minValue and arcid <= :maxValue";
        queryProvider.setSqlQuery(sql);
        queryProvider.setEntityClass(Article.class);
        reader.setQueryProvider(queryProvider);
        Map queryParames= new HashMap();
        queryParames.put("minValue",minValue);
        queryParames.put("maxValue",maxValue);
        reader.setParameterValues(queryParames);
        reader.setEntityManagerFactory(entityManagerFactory);
        return  reader;
    }
    //配置默认的轮询方式
/*   @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(10));
        return pollerMetadata;
    }*/

    //PollableChannel 具备轮询获得消息的能力。
    @Bean
    public PollableChannel outboundStaging() {
        return new NullChannel();
    }
}
