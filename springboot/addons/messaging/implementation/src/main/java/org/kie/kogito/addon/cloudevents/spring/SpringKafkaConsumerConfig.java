/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addon.cloudevents.spring;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class SpringKafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    String kafkaBootstrapAddress;
    @Value(value = "${spring.kafka.consumer.group-id}")
    String groupId;
    @Value(value = "${spring.kafka.consumer.auto-offset-reset}")
    String offset;
    @Value(value = "${spring.kafka.security.protocol}")
    String security_protocol;
    @Value(value = "${spring.kafka.ssl.trust-store-location}")
    String ts_location;
    @Value(value = "${spring.kafka.ssl.trust-store-password}")
    String ts_password;
    @Value(value = "${spring.kafka.ssl.key-store-location}")
    String ks_location;
    @Value(value = "${spring.kafka.ssl.key-store-password}")
    String ks_password;

    private static final Logger logger = LoggerFactory.getLogger(SpringKafkaConsumerConfig.class);

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        logger.info("Creating consumer factory with bootstrap {} and groupid {}", kafkaBootstrapAddress, groupId);
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, security_protocol);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ts_location);
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, ts_password);
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, ks_location);
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, ks_password);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
}