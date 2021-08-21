package com.atguigu;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkCDC {

    public static void main(String[] args) throws Exception {

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //2.通过FlinkCDC构建SourceFunction并读取数据
        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder()
                .hostname("hadoop102")
                .port(3306)
                .username("root")
                .password("000000")
                .databaseList("gmall-210325-flink")
                //.tableList("gmall-210325-flink.base_trademark")   //如果不添加该参数,则消费指定数据库中所有表的数据.如果指定,指定方式为db.table
                .deserializer(new StringDebeziumDeserializationSchema())
                .startupOptions(StartupOptions.latest())
                .build();
        DataStreamSource<String> streamSource = env.addSource(sourceFunction);

        //3.打印数据
        streamSource.print();

        //4.启动任务
        env.execute("FlinkCDC");

    }

}
