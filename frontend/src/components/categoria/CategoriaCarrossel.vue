<template>
  <section class="carousel-section py-5">
    <div class="container position-relative px-4">
      <h2 class="text-center mb-5">Explore por Categoria</h2>
      <button
        class="nav-btn left-btn"
        @click="scrollEsquerda"
        aria-label="Rolar para esquerda"
      >
        <i class="ph ph-caret-left fs-4"></i>
      </button>

      <div class="cat-scroll-container" ref="scrollContainer">
        <div
          v-for="categoria in categorias"
          :key="categoria.id"
          class="cat-scroll-item"
        >
          <CategoriaCard :categoria="categoria" />
        </div>
      </div>

      <button
        class="nav-btn right-btn"
        @click="scrollDireita"
        aria-label="Rolar para direita"
      >
        <i class="ph ph-caret-right fs-4"></i>
      </button>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import CategoriaCard from "./CategoriaCard.vue";

defineProps({
  categorias: {
    type: Array,
    required: true,
  },
});

// Referência para acessar a div no DOM
const scrollContainer = ref(null);

// Função para rolar para a esquerda
const scrollEsquerda = () => {
  if (scrollContainer.value) {
    // Rola 240px (tamanho do card + gap) suavemente
    scrollContainer.value.scrollBy({ left: -240, behavior: "smooth" });
  }
};

// Função para rolar para a direita
const scrollDireita = () => {
  if (scrollContainer.value) {
    scrollContainer.value.scrollBy({ left: 240, behavior: "smooth" });
  }
};
</script>

<style scoped>
h2 {
  font-size: 2.2rem;
  font-weight: 600;
}

/* Altere o container para dar mais espaço de respiro na altura */
.cat-scroll-container {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  min-height: 340px; /* Aumentado de 250px para 340px */
  padding: 20px 10px;
  margin: -20px -10px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* Altere a largura fixa do card aqui */
.cat-scroll-item {
  flex: 0 0 280px; /* Aumentado de 220px para 280px (deixa o card mais largo) */
  scroll-snap-align: center;
}

/* Esconde a barra de rolagem no Chrome/Safari/Edge */
.cat-scroll-container::-webkit-scrollbar {
  display: none;
}

/* Estilo dos botões flutuantes */
.nav-btn {
  position: absolute;
  top: 60%; /* Posiciona no meio da altura dos cards */
  transform: translateY(-50%);
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: white;
  border: 1px solid #eaeaea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
  color: #333;
}

.nav-btn:hover {
  background: #f8f9fa;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.left-btn {
  left: -5px; /* Fica levemente fora da margem esquerda */
}

.right-btn {
  right: -5px; /* Fica levemente fora da margem direita */
}
</style>
