package pe.com.ci.sed.web.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import pe.com.ci.sed.web.errors.CustomAccessDeniedHandler;
import pe.com.ci.sed.web.errors.CustomAuthenticationEntryPoint;

@Configuration
@AllArgsConstructor
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("v1");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        final String context = "/v1/";
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher(context + "**")
                .authorizeRequests()
//                .antMatchers(context + "catalogo/**").hasAnyAuthority("1", "USER")
//                .antMatchers(context + "usuario/**").hasAnyAuthority("1")
//                .antMatchers(context + "privilegio/**").hasAnyAuthority("1")
//                .antMatchers(context + "rol/**").hasAnyAuthority("1")
                .antMatchers(context + "**").authenticated()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);
    }
}
