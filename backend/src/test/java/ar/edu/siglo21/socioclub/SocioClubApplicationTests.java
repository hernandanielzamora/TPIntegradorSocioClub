package ar.edu.siglo21.socioclub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import ar.edu.siglo21.socioclub.controller.mvc.InicioController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba acotada a MVC: no levanta JPA ni simula una conexión MySQL.
@WebMvcTest(InicioController.class)
class SocioClubApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void muestraLaPantallaInicialDeSecretaria() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("secretaria/inicio"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sistema de secretaría")));
    }

}
