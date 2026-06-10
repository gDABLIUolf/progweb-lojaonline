<template>
  <div class="card shadow-sm border-0 h-100 produto-card">
    <div class="produto-img-container position-relative">
      <img
        v-if="temImagem"
        :src="imagemUrl"
        :alt="produto.nome"
        class="produto-img"
        @error="handleImageError"
      />
      <div
        v-else
        class="produto-img d-flex align-items-center justify-content-center bg-light"
      >
        <span class="text-muted small">Sem foto</span>
      </div>

      <span
        v-if="produto.categoriasNomes?.length"
        class="badge bg-dark position-absolute top-0 start-0 m-3"
      >
        {{ produto.categoriasNomes[0] }}
      </span>
    </div>

    <div class="card-body d-flex flex-column">
      <h3 class="produto-titulo mb-2">{{ produto.nome }}</h3>

      <p class="text-muted small mb-3 flex-grow-1 produto-descricao">
        {{ produto.descricao }}
      </p>

      <div class="mt-auto">
        <div class="mb-2">
          <span class="preco fw-bold fs-5">R$ {{ precoFormatado }}</span>
        </div>
        <button
          class="btn btn-dark rounded-pill w-100 py-2 btn-comprar"
          @click="$emit('adicionar-carrinho', produto.id)"
        >
          Adicionar ao carrinho
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";

const props = defineProps({
  produto: {
    type: Object,
    required: true,
  },
});

defineEmits(["adicionar-carrinho"]);

const temImagem = ref(true);

const precoFormatado = computed(() => {
  return Number(props.produto.preco).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
  });
});

const imagemUrl = computed(() => {
  return `http://localhost:8080/api/produtos/${props.produto.id}/imagem?t=${new Date().getTime()}`;
});

const handleImageError = () => {
  temImagem.value = false;
};
</script>

<style scoped>
.produto-card {
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.produto-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1) !important;
}

.produto-img-container {
  height: 280px;
  overflow: hidden;
  background: #f8f9fa;
}

.produto-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.produto-card:hover .produto-img {
  transform: scale(1.05);
}

.produto-titulo {
  font-size: 1.15rem;
  font-weight: 600;
  color: #222;
}

/* Trunca a descrição para não quebrar o layout se for muito grande */
.produto-descricao {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.btn-comprar {
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-comprar:hover {
  background: #333;
  transform: scale(1.05);
}
</style>
