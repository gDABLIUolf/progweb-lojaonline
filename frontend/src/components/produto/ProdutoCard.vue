<template>
  <div class="card shadow-sm border-0 h-100 produto-card">
    <RouterLink :to="'/produto/' + produto.id" class="text-decoration-none">
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
          v-if="produto.desconto > 0"
          class="badge bg-danger position-absolute top-0 end-0 m-3"
        >
          -{{ produto.desconto }}%
        </span>
      </div>
    </RouterLink>

    <div class="card-body d-flex flex-column">
      <RouterLink :to="'/produto/' + produto.id" class="text-decoration-none text-dark">
        <h3 class="produto-titulo mb-2">{{ produto.nome }}</h3>
      </RouterLink>

      <p class="text-muted small mb-3 flex-grow-1 produto-descricao">
        {{ produto.descricao }}
      </p>

      <div class="mt-auto">
        <div class="mb-2 d-flex align-items-center gap-2 flex-wrap">
          <template v-if="produto.desconto > 0">
            <span class="preco-antigo text-decoration-line-through">R$ {{ precoFormatado }}</span>
            <span class="preco fw-bold fs-5 text-danger">R$ {{ precoPromocionalFormatado }}</span>
          </template>
          <template v-else>
            <span class="preco fw-bold fs-5">R$ {{ precoFormatado }}</span>
          </template>
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

const precoPromocionalFormatado = computed(() => {
  if (!props.produto.desconto) return "";
  const precoOriginal = Number(props.produto.preco);
  const precoFinal = precoOriginal * (1 - props.produto.desconto / 100);
  return precoFinal.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
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
  background: var(--surface-color, #f8f9fa);
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
  color: var(--text-primary, #222);
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
  opacity: 0.9;
  transform: scale(1.05);
}

.preco-antigo {
  font-size: 0.9rem;
  color: var(--text-secondary, #86868b);
}

.text-danger {
  color: #e53e3e !important;
}
</style>
