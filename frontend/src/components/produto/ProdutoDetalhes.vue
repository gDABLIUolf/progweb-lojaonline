<template>
  <div class="d-flex flex-column text-start">
    <!-- Categorias -->
    <div class="mb-3 d-flex flex-wrap gap-2">
      <span
        v-for="cat in produto.categoriasNomes"
        :key="cat"
        class="badge bg-dark px-3 py-2 fw-normal"
      >
        {{ cat }}
      </span>
    </div>

    <!-- Nome -->
    <h1 class="fw-bold mb-3 display-5 text-dark">{{ produto.nome }}</h1>

    <!-- Estrelas / Média de Avaliações -->
    <div class="d-flex align-items-center mb-4">
      <div class="d-flex align-items-center" style="gap: 3px;">
        <span
          v-for="n in 5"
          :key="n"
          style="position: relative; display: inline-flex; width: 20px; height: 20px; flex-shrink: 0;"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" style="width:20px;height:20px;display:block;">
            <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z" fill="#e0e0e0" />
          </svg>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 256 256"
            style="position:absolute;top:0;left:0;width:20px;height:20px;display:block;"
            :style="{ clipPath: `inset(0 ${100 - calcularPorcentagem(produto.mediaAvaliacoes || 0, n)}% 0 0)` }"
          >
            <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z" fill="#ffc107" />
          </svg>
        </span>
      </div>
      <span class="text-muted ms-2 small fw-bold mt-1">
        {{ (produto.mediaAvaliacoes || 0).toFixed(1).replace('.', ',') }}
        <span class="fw-normal">({{ produto.avaliacoes?.length || 0 }} avaliações)</span>
      </span>
    </div>

    <!-- Preço -->
    <div class="mb-4 d-flex align-items-center gap-3 flex-wrap">
      <template v-if="produto.desconto > 0">
        <span class="preco-antigo text-decoration-line-through text-muted fs-4">
          R$ {{ precoFormatado }}
        </span>
        <span class="preco-valor display-6 fw-bold text-danger">
          R$ {{ precoPromocionalFormatado }}
        </span>
        <span class="badge bg-danger rounded-pill px-3 py-2 fw-normal fs-6">
          -{{ produto.desconto }}%
        </span>
      </template>
      <template v-else>
        <span class="preco-valor display-6 fw-bold">R$ {{ precoFormatado }}</span>
      </template>
    </div>

    <!-- Descrição -->
    <div class="mb-5">
      <h5 class="fw-bold text-uppercase text-muted small mb-2">Descrição</h5>
      <p class="text-secondary lh-lg" style="font-size: 1.05rem;">
        {{ produto.descricao }}
      </p>
    </div>

    <!-- Seletor de Quantidade + Botão Adicionar -->
    <div class="d-flex align-items-center gap-3 mt-auto">
      <div class="qty-wrapper d-flex align-items-center rounded-pill px-2 py-1">
        <button class="btn-qty" @click="decrementar">
          <i class="ph ph-minus"></i>
        </button>
        <span class="fw-bold px-3" style="min-width: 40px; text-align: center;">
          {{ quantidade }}
        </span>
        <button class="btn-qty" @click="incrementar">
          <i class="ph ph-plus"></i>
        </button>
      </div>

      <button
        class="btn btn-dark rounded-pill px-5 py-3 fw-bold flex-grow-1"
        @click="$emit('adicionar-carrinho', produto.id, quantidade)"
      >
        Adicionar ao carrinho
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  produto: {
    type: Object,
    required: true,
  },
});

defineEmits(["adicionar-carrinho"]);

const quantidade = ref(1);

const incrementar = () => quantidade.value++;
const decrementar = () => { if (quantidade.value > 1) quantidade.value--; };

const precoFormatado = computed(() =>
  Number(props.produto.preco).toLocaleString("pt-BR", { minimumFractionDigits: 2 })
);

const precoPromocionalFormatado = computed(() => {
  if (!props.produto.desconto) return "";
  const final = Number(props.produto.preco) * (1 - props.produto.desconto / 100);
  return final.toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
});

const calcularPorcentagem = (media, posicao) => {
  const m = parseFloat(media);
  const preenchimento = m - (posicao - 1);
  if (preenchimento >= 1) return 100;
  if (preenchimento <= 0) return 0;
  return Math.round(preenchimento * 100);
};
</script>

<style scoped>
.preco-valor { color: var(--primary-color); }
.preco-antigo { color: var(--text-secondary, #86868b); }
.text-danger { color: #e53e3e !important; }

/* Seletor de quantidade — usa variáveis do tema para funcionar no dark mode */
.qty-wrapper {
  background: var(--surface-color, #f8fafc);
  border: 1px solid var(--border-color, #e2e8f0);
}

.btn-qty {
  background: transparent;
  border: none;
  font-size: 1.1rem;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.2s;
  color: var(--text-primary, inherit);
}

.btn-qty:hover { opacity: 0.6; }
</style>
