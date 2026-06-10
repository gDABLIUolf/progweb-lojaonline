<template>
  <footer id="footer" class="footer-premium">
    <div class="container">
      <div class="footer-grid">
        <!-- Brand Info -->
        <div class="footer-brand">
          <h3 class="logo-footer">vesteBem.</h3>
          <p class="brand-desc">
            O essencial, elevated. Roupas com cortes atemporais, tecidos sustentáveis e qualidade incomparável.
          </p>
          <div class="social-links">
            <a :href="contato.linkInstagram" target="_blank" title="Instagram"><i class="ph ph-instagram-logo"></i></a>
            <a :href="contato.linkFacebook" target="_blank" title="Facebook"><i class="ph ph-facebook-logo"></i></a>
          </div>
        </div>

        <!-- Contact Section -->
        <div class="footer-contact" id="contato-secao">
          <h4>Contato</h4>
          <ul class="contact-list">
            <li>
              <i class="ph ph-phone"></i>
              <span>{{ contato.telefone }}</span>
            </li>
            <li>
              <i class="ph ph-envelope"></i>
              <span>{{ contato.email }}</span>
            </li>
            <li>
              <i class="ph ph-map-pin"></i>
              <span>{{ contato.endereco }}</span>
            </li>
            <li>
              <i class="ph ph-clock"></i>
              <span>{{ contato.horario }}</span>
            </li>
          </ul>
        </div>

        <!-- Newsletter / Promotions Section -->
        <div class="footer-newsletter" id="newsletter-secao">
          <h4>Promoções & Novidades</h4>
          <p>Assine nossa newsletter e receba 10% de desconto na sua primeira compra, além de ofertas exclusivas.</p>
          <form @submit.prevent="inscreverNewsletter" class="newsletter-form">
            <input 
              type="email" 
              placeholder="Seu melhor e-mail" 
              required 
              v-model="emailNewsletter"
              class="form-control-newsletter"
            />
            <button type="submit" class="btn-newsletter" title="Inscrever-se">
              <i class="ph ph-paper-plane-right"></i>
            </button>
          </form>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; 2026 vesteBem. Todos os direitos reservados. Feito com amor.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "../../services/api.js";

const emailNewsletter = ref("");
const contato = ref({
  telefone: "(11) 99999-8888",
  email: "suporte@vestebem.com.br",
  endereco: "Av. Paulista, 1000 - São Paulo, SP",
  horario: "Seg - Sex: 9h às 18h",
  linkFacebook: "https://facebook.com",
  linkInstagram: "https://instagram.com"
});

const carregarContato = async () => {
  try {
    const resposta = await api.get("/contato");
    if (resposta.data) {
      contato.value = resposta.data;
    }
  } catch (error) {
    console.error("Erro ao carregar contatos no footer:", error);
  }
};

const inscreverNewsletter = () => {
  if (emailNewsletter.value) {
    alert(`Obrigado por se inscrever! Enviamos um cupom de 10% de desconto para ${emailNewsletter.value}`);
    emailNewsletter.value = "";
  }
};

onMounted(() => {
  carregarContato();
});
</script>

<style scoped>
.footer-premium {
  background: #111111;
  color: #ffffff;
  padding: 5rem 0 2rem 0;
  margin-top: 5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.footer-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1.5fr;
  gap: 4rem;
  margin-bottom: 4rem;
}

.logo-footer {
  font-size: 1.8rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 1.5rem;
}

.brand-desc {
  color: #86868b;
  font-size: 0.95rem;
  font-weight: 300;
  line-height: 1.6;
  margin-bottom: 1.5rem;
  max-width: 90%;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-links a {
  color: #ffffff;
  font-size: 1.4rem;
  transition: all 0.3s ease;
}

.social-links a:hover {
  color: #86868b;
  transform: translateY(-2px);
}

.footer-contact h4,
.footer-newsletter h4 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  letter-spacing: -0.01em;
}

.contact-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.contact-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #86868b;
  font-size: 0.95rem;
  margin-bottom: 12px;
}

.contact-list li i {
  font-size: 1.2rem;
  color: #ffffff;
}

.footer-newsletter p {
  color: #86868b;
  font-size: 0.95rem;
  font-weight: 300;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.newsletter-form {
  display: flex;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 100px;
  padding: 4px;
}

.form-control-newsletter {
  background: transparent;
  border: none;
  color: #ffffff;
  padding: 10px 16px;
  font-size: 0.9rem;
  width: 100%;
  outline: none;
}

.form-control-newsletter::placeholder {
  color: #555555;
}

.btn-newsletter {
  background: #ffffff;
  color: #111111;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-newsletter:hover {
  background: #f5f5f7;
  transform: scale(1.05);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 2rem;
  text-align: center;
}

.footer-bottom p {
  color: #555555;
  font-size: 0.85rem;
  margin: 0;
}

.highlight-pulse {
  animation: pulseHighlight 1s ease-in-out 2;
}

@keyframes pulseHighlight {
  0% {
    text-shadow: 0 0 0px rgba(255, 255, 255, 0);
  }
  50% {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
    color: #ffffff;
  }
  100% {
    text-shadow: 0 0 0px rgba(255, 255, 255, 0);
  }
}

@media (max-width: 992px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .brand-desc {
    max-width: 100%;
  }
}
</style>
