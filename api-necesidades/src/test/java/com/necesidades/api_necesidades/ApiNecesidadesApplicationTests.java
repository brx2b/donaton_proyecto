package com.necesidades.api_necesidades;

import com.necesidades.api_necesidades.client.UsuarioCliente;
import com.necesidades.api_necesidades.config.TestCacheConfiguration;
import com.necesidades.api_necesidades.service.NecesidadesService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration"
})
@Import(TestCacheConfiguration.class)
class ApiNecesidadesApplicationTests {
    @MockitoBean
    private UsuarioCliente usuarioClient;

    @MockitoBean
    private NecesidadesService necesidadesService;
	@Test
	void contextLoads() {
	}

}
