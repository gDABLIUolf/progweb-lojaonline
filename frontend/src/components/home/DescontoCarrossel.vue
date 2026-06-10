<template>
  <section v-if="slides.length > 0" id="promocoes-secao" class="discount-carousel-section py-5">
    <div class="container">
      <div 
        class="carousel-container" 
        @mouseenter="pausarAutoPlay" 
        @mouseleave="iniciarAutoPlay"
      >
        <!-- Slides com Transição -->
        <transition name="slide-fade" mode="out-in">
          <div 
            :key="slideAtivo" 
            class="carousel-slide"
            @click="irParaProduto(slides[slideAtivo])"
          >
            <!-- Lado da Imagem -->
            <div class="slide-image-wrap">
              <img 
                :src="slides[slideAtivo].imagem" 
                :alt="slides[slideAtivo].nomeExibicao" 
                class="slide-image"
              />
              <div class="discount-badge" v-if="slides[slideAtivo].descontoPct > 0">
                <span class="discount-percent">{{ slides[slideAtivo].descontoPct }}%</span>
                <span class="discount-text">OFF</span>
              </div>
            </div>

            <!-- Lado do Conteúdo -->
            <div class="slide-content">
              <span class="slide-tag">{{ slides[slideAtivo].tag }}</span>
              <h2 class="slide-title">{{ slides[slideAtivo].nomeExibicao }}</h2>
              
              <div class="slide-price-container">
                <span class="price-old">De R$ {{ formatarPreco(slides[slideAtivo].precoOriginal) }}</span>
                <span class="price-current">
                  Por <span class="price-value">R$ {{ formatarPreco(slides[slideAtivo].precoDesconto) }}</span>
                </span>
              </div>

              <div class="slide-actions">
                <button 
                  class="btn-promo-cta"
                  @click.stop="irParaProduto(slides[slideAtivo])"
                >
                  Aproveitar Desconto
                  <i class="ph ph-arrow-right ms-2"></i>
                </button>
              </div>
            </div>
          </div>
        </transition>

        <!-- Botões de Navegação Manual (Setas) -->
        <button 
          v-if="slides.length > 1"
          class="nav-arrow prev-arrow" 
          @click.stop="slideAnterior" 
          aria-label="Slide anterior"
        >
          <i class="ph ph-caret-left"></i>
        </button>
        <button 
          v-if="slides.length > 1"
          class="nav-arrow next-arrow" 
          @click.stop="proximoSlide" 
          aria-label="Próximo slide"
        >
          <i class="ph ph-caret-right"></i>
        </button>

        <!-- Indicadores (Dots) -->
        <div v-if="slides.length > 1" class="carousel-dots">
          <button 
            v-for="(slide, index) in slides" 
            :key="index"
            class="carousel-dot"
            :class="{ active: index === slideAtivo }"
            @click.stop="definirSlide(index)"
            :aria-label="'Ir para slide ' + (index + 1)"
          ></button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api.js";

const router = useRouter();
const slideAtivo = ref(0);
const autoPlayIntervalId = ref(null);
const slides = ref([]);

// Auxiliar para formatar preços com pontuação pt-BR
const formatarPreco = (valor) => {
  return Number(valor).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

// Carregar slides em promoção em destaque do carrossel
const carregarSlides = async () => {
  try {
    const resposta = await api.get("/produtos?destaqueCarrossel=true");
    const produtosDestaque = resposta.data || [];
    
    slides.value = produtosDestaque.map(p => {
      const precoOriginal = Number(p.preco);
      const descontoPct = p.desconto || 0;
      const precoDesconto = precoOriginal * (1 - descontoPct / 100);
      
      // Determinar a URL da imagem promocional customizada no banco de dados
      const imagem = `http://localhost:8080/api/produtos/${p.id}/imagem-carrossel`;
      
      // Tags customizadas baseadas no nome do produto
      let tag = "DESTAQUE EXCLUSIVO";
      if (p.nome.toLowerCase().includes("sobretudo")) tag = "DESTAQUE DA SEMANA";
      else if (p.nome.toLowerCase().includes("suéter") || p.nome.toLowerCase().includes("cashmere")) tag = "CONFORTO PREMIUM EXCLUSIVO";
      else if (p.nome.toLowerCase().includes("camisa") || p.nome.toLowerCase().includes("seda")) tag = "COLEÇÃO SEDA FINA";

      return {
        id: p.id,
        nomeExibicao: p.nome,
        descontoPct: descontoPct,
        precoOriginal: precoOriginal,
        precoDesconto: precoDesconto,
        imagem: imagem,
        tag: tag
      };
    });
    
    // Iniciar autoplay se tivermos slides
    if (slides.value.length > 1) {
      iniciarAutoPlay();
    }
  } catch (error) {
    console.error("Erro ao carregar slides do carrossel:", error);
  }
};

// Funções de navegação do carrossel
const proximoSlide = () => {
  if (slides.value.length === 0) return;
  slideAtivo.value = (slideAtivo.value + 1) % slides.value.length;
};

const slideAnterior = () => {
  if (slides.value.length === 0) return;
  slideAtivo.value = (slideAtivo.value - 1 + slides.value.length) % slides.value.length;
};

const definirSlide = (index) => {
  slideAtivo.value = index;
};

// Roteamento para a tela do produto
const irParaProduto = (slide) => {
  if (slide.id) {
    router.push(`/produto/${slide.id}`);
  }
};

// Temporizador da auto-rotação
const iniciarAutoPlay = () => {
  if (slides.value.length <= 1) return;
  pausarAutoPlay();
  autoPlayIntervalId.value = setInterval(proximoSlide, 5000);
};

const pausarAutoPlay = () => {
  if (autoPlayIntervalId.value) {
    clearInterval(autoPlayIntervalId.value);
    autoPlayIntervalId.value = null;
  }
};

onMounted(() => {
  carregarSlides();
});

onUnmounted(() => {
  pausarAutoPlay();
});
</script>

<style scoped>
.discount-carousel-section {
  background-color: transparent;
}

.carousel-container {
  position: relative;
  background: #111111; /* Cor escura premium correspondente ao rodapé */
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  height: 450px;
  min-height: 450px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.carousel-slide {
  display: flex;
  flex-direction: row;
  height: 450px;
  min-height: 450px;
  cursor: pointer;
  width: 100%;
}

/* Coluna de Imagem Promocional */
.slide-image-wrap {
  flex: 1.5;
  position: relative;
  overflow: hidden;
  height: 100%;
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s cubic-bezier(0.25, 1, 0.5, 1);
}

.carousel-slide:hover .slide-image {
  transform: scale(1.03);
}

/* Badge de Desconto Moderno na Imagem */
.discount-badge {
  position: absolute;
  top: 24px;
  left: 24px;
  background: var(--bg-color, #ffffff);
  color: #111111;
  padding: 10px 18px;
  border-radius: var(--radius-sm);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  font-weight: 700;
  line-height: 1;
  z-index: 2;
}

.discount-percent {
  font-size: 1.8rem;
  letter-spacing: -0.02em;
}

.discount-text {
  font-size: 0.7rem;
  letter-spacing: 0.1em;
  opacity: 0.8;
  margin-top: 2px;
}

/* Coluna de Conteúdo da Oferta */
.slide-content {
  flex: 1;
  padding: 3rem 4rem;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
  z-index: 1;
}

.slide-tag {
  font-size: 0.8rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--text-secondary, #86868b);
  font-weight: 600;
  margin-bottom: 1rem;
}

.slide-title {
  font-size: clamp(1.8rem, 3.5vw, 2.5rem);
  font-weight: 600;
  line-height: 1.15;
  letter-spacing: -0.02em;
  margin-bottom: 1.5rem;
  color: #ffffff;
}

.slide-price-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 2rem;
}

.price-old {
  font-size: 1rem;
  text-decoration: line-through;
  color: var(--text-secondary, #86868b);
}

.price-current {
  font-size: 1.15rem;
  color: rgba(255, 255, 255, 0.9);
}

.price-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #ffffff;
}

.btn-promo-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.9rem 2.2rem;
  font-weight: 500;
  border-radius: var(--radius-pill);
  transition: var(--transition);
  background: #ffffff;
  color: #111111;
  border: none;
  font-size: 0.95rem;
}

.btn-promo-cta:hover {
  background: #f5f5f7;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.15);
}

/* Setas de Navegação */
.nav-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.05);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 3;
  transition: all 0.3s ease;
  font-size: 1.2rem;
}

.nav-arrow:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-50%) scale(1.05);
}

.prev-arrow {
  left: 20px;
}

.next-arrow {
  right: 20px;
}

/* Dots Indicadores */
.carousel-dots {
  position: absolute;
  bottom: 20px;
  right: 4.5rem;
  display: flex;
  gap: 8px;
  z-index: 3;
}

.carousel-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  border: none;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-dot.active {
  background: #ffffff;
  width: 20px;
  border-radius: 100px;
}

/* Animações de Transição (slide-fade) */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(15px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}

/* Responsividade */
@media (max-width: 992px) {
  .carousel-slide {
    flex-direction: column;
    min-height: auto;
  }

  .slide-image-wrap {
    width: 100%;
    aspect-ratio: 16 / 10;
  }

  .slide-content {
    padding: 2.5rem;
    text-align: center;
  }

  .slide-price-container {
    align-items: center;
    margin-bottom: 1.5rem;
  }

  .carousel-dots {
    right: 50%;
    transform: translateX(50%);
    bottom: 15px;
  }

  .nav-arrow {
    top: 35%; /* Posiciona no meio da imagem */
  }
}

@media (max-width: 576px) {
  .carousel-container {
    border-radius: var(--radius-md);
  }
  
  .slide-content {
    padding: 2rem 1.5rem;
  }

  .slide-title {
    font-size: 1.6rem;
  }

  .discount-badge {
    top: 16px;
    left: 16px;
    padding: 8px 12px;
  }

  .discount-percent {
    font-size: 1.4rem;
  }

  .nav-arrow {
    width: 36px;
    height: 36px;
    font-size: 1rem;
  }
}
</style>
