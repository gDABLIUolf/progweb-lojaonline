<template>
  <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white">
    <!-- Cabeçalho com controles de seleção -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Itens do Carrinho</h4>
      <button
        class="btn-selecao"
        :class="{ 'btn-selecao--outline': todosSelecionados }"
        @click="$emit('toggle-todos')"
      >
        <i class="ph me-1" :class="todosSelecionados ? 'ph-check-square' : 'ph-square'"></i>
        {{ todosSelecionados ? 'Desmarcar tudo' : 'Selecionar tudo' }}
      </button>
    </div>

    <!-- Contagem de selecionados -->
    <div class="selecao-info mb-3">
      <i class="ph ph-check-circle me-1"></i>
      {{ itensSelecionados.length }} de {{ itens.length }}
      {{ itensSelecionados.length === 1 ? 'item selecionado' : 'itens selecionados' }}
    </div>

    <!-- Linhas de item -->
    <div
      v-for="item in itens"
      :key="item.itemId"
      class="item-row d-flex align-items-center gap-3 py-3"
      :class="{ 'item-row--selecionado': itensSelecionados.includes(item.itemId) }"
    >
      <!-- Checkbox de seleção -->
      <div class="checkbox-wrapper" @click="$emit('toggle-selecao', item.itemId)">
        <div class="custom-checkbox" :class="{ 'custom-checkbox--checked': itensSelecionados.includes(item.itemId) }">
          <i class="ph ph-check" v-if="itensSelecionados.includes(item.itemId)"></i>
        </div>
      </div>

      <img
        :src="`http://localhost:8080/api/produtos/${item.produtoId}/imagem`"
        :alt="item.produtoNome"
        class="item-img rounded-3"
      />

      <div class="flex-grow-1">
        <h6 class="fw-bold mb-1">{{ item.produtoNome }}</h6>
        <div class="d-flex align-items-center gap-2 flex-wrap">
          <template v-if="item.desconto > 0">
            <span class="text-decoration-line-through text-muted small">
              R$ {{ item.precoOriginal.toFixed(2) }}
            </span>
            <span class="fw-bold text-danger small">
              R$ {{ item.precoUnitario.toFixed(2) }} cada
            </span>
            <span class="badge bg-danger rounded-pill px-2" style="font-size: 0.7rem; line-height: 1; padding-top: 0.25rem; padding-bottom: 0.25rem;">
              -{{ item.desconto }}%
            </span>
          </template>
          <template v-else>
            <span class="text-muted small">
              R$ {{ item.precoUnitario.toFixed(2) }} cada
            </span>
          </template>
        </div>
      </div>

      <!-- Controle de quantidade -->
      <div class="d-flex align-items-center gap-2">
        <button
          class="btn-qty"
          @click="$emit('remover', item.produtoId)"
          :disabled="atualizando"
        >
          <i class="ph ph-minus"></i>
        </button>
        <span class="fw-bold px-2">{{ item.quantidade }}</span>
        <button
          class="btn-qty"
          @click="$emit('adicionar', item.produtoId)"
          :disabled="atualizando"
        >
          <i class="ph ph-plus"></i>
        </button>
      </div>

      <!-- Total do item -->
      <div class="text-end" style="min-width: 90px">
        <p class="fw-bold mb-0" style="color: var(--primary-color)">
          R$ {{ item.precoTotal.toFixed(2) }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  itens: {
    type: Array,
    default: () => [],
  },
  itensSelecionados: {
    type: Array,
    default: () => [],
  },
  todosSelecionados: {
    type: Boolean,
    default: false,
  },
  atualizando: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["toggle-selecao", "toggle-todos", "adicionar", "remover"]);
</script>

<style scoped>
.carrinho-card {
  border: 1px solid #f1f5f9;
}

/* ── Seleção ── */
.btn-selecao {
  display: inline-flex;
  align-items: center;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 0.35rem 0.85rem;
  border-radius: 999px;
  border: 2px solid #1a1a1a;
  background: #f5f5f7;
  color: #1a1a1a;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-selecao:hover {
  background: #e8e8ed;
}

.btn-selecao--outline {
  background: #1a1a1a;
  color: #ffffff;
  border-color: #1a1a1a;
}

.btn-selecao--outline:hover {
  background: #333;
  border-color: #333;
}

.selecao-info {
  font-size: 0.82rem;
  color: var(--primary-color);
  font-weight: 600;
  background: color-mix(in srgb, var(--primary-color) 8%, transparent);
  border-radius: 8px;
  padding: 0.4rem 0.8rem;
  display: inline-block;
}

/* ── Checkbox ── */
.checkbox-wrapper {
  cursor: pointer;
  flex-shrink: 0;
}

.custom-checkbox {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  border: 2px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  font-size: 0.75rem;
  color: white;
}

.custom-checkbox--checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

/* ── Item Row ── */
.item-row {
  border-bottom: 1px solid #f1f5f9;
  border-radius: 12px;
  transition: background 0.2s;
  padding-left: 0.5rem;
  padding-right: 0.5rem;
}

.item-row:last-child {
  border-bottom: none;
}

.item-row--selecionado {
  background: color-mix(in srgb, var(--primary-color) 4%, transparent);
}

.item-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  flex-shrink: 0;
}

/* ── Quantidade ── */
.btn-qty {
  width: 32px;
  height: 32px;
  border: 1px solid #e2e8f0;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
}

.btn-qty:hover:not(:disabled) {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.btn-qty:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
