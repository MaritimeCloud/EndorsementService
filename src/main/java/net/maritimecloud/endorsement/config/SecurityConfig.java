/*
 * Copyright 2017 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.maritimecloud.endorsement.config;

import net.maritimecloud.endorsement.utils.AccessControlUtil;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public AccessControlUtil accessControlUtil() {
        return new AccessControlUtil();
    }

    @Configuration
    @Order(1)
    public static class OIDCWebSecurityConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter
    {
        /**
         * Registers the MCKeycloakAuthenticationProvider with the authentication manager.
         */
        /*@Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(mcKeycloakAuthenticationProvider());
        }*/

        /*@Bean
        protected MCKeycloakAuthenticationProvider mcKeycloakAuthenticationProvider() {
            return new MCKeycloakAuthenticationProvider();
        }*/

        /**
         * Defines the session authentication strategy.
         */
        @Bean
        @Override
        protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
            // When using as confidential keycloak/OpenID Connect client:
            //return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
            // When using as bearer-only keycloak/OpenID Connect client:
            return new NullAuthenticatedSessionStrategy();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            super.configure(http);
            http
                .addFilterBefore(new SimpleCorsFilter(), ChannelProcessingFilter.class)
                .csrf().disable()
                .requestMatchers()
                    .antMatchers("/oidc/**","/sso/**") // "/sso/**" matches the urls used by the keycloak adapter
            .and()
                .authorizeRequests()
                    //.expressionHandler(webExpressionHandler())
                    // Some general filters for access, more specific ones are set at each method
                    .antMatchers(HttpMethod.POST, "/oidc/endorsements").authenticated()
                    .antMatchers(HttpMethod.GET, "/oidc/endorsements/**").authenticated()
                    .antMatchers(HttpMethod.GET, "/oidc/endorsements-by/**").authenticated()
                    .antMatchers(HttpMethod.DELETE, "/oidc/endorsements/**").authenticated()
            ;
        }

        @Bean
        public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
                KeycloakAuthenticationProcessingFilter filter) {
            FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
            registrationBean.setEnabled(false);
            return registrationBean;
        }

        @Bean
        public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
                KeycloakPreAuthActionsFilter filter) {
            FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
            registrationBean.setEnabled(false);
            return registrationBean;
        }

        /*@Bean
        public RoleHierarchy roleHierarchy() {
            RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
            // If the hierarchy is changed, remember to update the hierarchy below and the list in
            // net.maritimecloud.identityregistry.controllers.RoleController:getAvailableRoles()
            roleHierarchy.setHierarchy("ROLE_SITE_ADMIN > ROLE_APPROVE_ORG\n" +
                    "ROLE_SITE_ADMIN > ROLE_ORG_ADMIN\n" +
                    "ROLE_ORG_ADMIN > ROLE_ENTITY_ADMIN\n" +
                    "ROLE_ENTITY_ADMIN > ROLE_USER_ADMIN\n" +
                    "ROLE_ENTITY_ADMIN > ROLE_VESSEL_ADMIN\n" +
                    "ROLE_ENTITY_ADMIN > ROLE_SERVICE_ADMIN\n" +
                    "ROLE_ENTITY_ADMIN > ROLE_DEVICE_ADMIN\n" +
                    "ROLE_USER_ADMIN > ROLE_USER\n" +
                    "ROLE_VESSEL_ADMIN > ROLE_USER\n" +
                    "ROLE_SERVICE_ADMIN > ROLE_USER\n" +
                    "ROLE_DEVICE_ADMIN > ROLE_USER");
            return roleHierarchy;
        }

        private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
            DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
            defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
            return defaultWebSecurityExpressionHandler;
        }*/
    }

}
