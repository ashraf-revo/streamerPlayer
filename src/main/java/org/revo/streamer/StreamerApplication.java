package org.revo.streamer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class StreamerApplication {


    public static void main(String[] args) {
        SpringApplication.run(StreamerApplication.class, args);
    }

    //ffmpeg -re -i input.mp4 -c:a aac   -vn -f rtp rtp://127.0.0.1:11111
    @Bean
    public IntegrationFlow aacIntegrationFlows() throws IOException {
        return IntegrationFlows
                .from(new UnicastReceivingChannelAdapter(11111))
                .handle(new AacMessageHandler())
                .get();
    }

    //ffmpeg -re -i input.mp4 -c:v h264  -an -f rtp rtp://127.0.0.1:11112
    @Bean
    public IntegrationFlow h264IntegrationFlows() throws IOException {
        return IntegrationFlows
                .from(new UnicastReceivingChannelAdapter(11112))
                .handle(new H264MessageHandler())
                .get();
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
    }
//    ffmpeg -re -i input.mp4 -c:v h264 -an -map 0:0  -f rtp rtp://127.0.0.1:11112  -c:a aac -vn  -map 0:1  -f rtp rtp://127.0.0.1:11111

}

