package com.kailing.bootbatch.partitionjob.config;
import com.kailing.bootbatch.partitionjob.support.BatchIncrementer;
import com.kailing.bootbatch.partitionjob.support.JobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by kl on 2018/3/1.
 * Content :Master节点，派发分片任务到从节点
 */
@Configuration
public class MasterConfiguration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Profile({"master", "mixed"})
    @Bean
    public Job job(@Qualifier("masterStep") Step masterStep) {
        return jobBuilderFactory.get("endOfDayjob")
                .start(masterStep)
                .incrementer(new BatchIncrementer())
                .listener(new JobListener())
                .build();
    }
/*
* 需要给ColumnRangePartitioner，partitionHandler，
* 从节点slave的名字这样就不会依赖了，slaveStep可以不注入直接给名字
 * .partitioner("slaveStep", new ColumnRangePartitioner(dataSource))

* */
    @Bean("masterStep")
    public Step masterStep(@Qualifier("slaveStep") Step slaveStep,
                           PartitionHandler partitionHandler,
                           DataSource dataSource) {
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep.getName(), new ColumnRangePartitioner(dataSource))
              //  .step(slaveStep)
                .partitionHandler(partitionHandler)
                .build();
    }
}
