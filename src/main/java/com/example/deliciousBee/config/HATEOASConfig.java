package com.example.deliciousBee.config;
import com.example.deliciousBee.dto.restaurant.RestaurantDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

//@Configuration
//@EnableHypermediaSupport(type = HypermediaType.HAL)
//public class HATEOASConfig {
//
//    @Bean
//    public PagedResourcesAssembler<RestaurantDto> pagedResourcesAssembler() {
//        return new PagedResourcesAssembler<>(null, null);
//    }
//
//    @Bean
//    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
//        return p -> p.setOneIndexedParameters(true);
//    }
//}