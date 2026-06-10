<template>
  <div class="sidebar-container">
    <!-- Overlay escuro atrás do sidebar -->
    <div
      v-if="isOpen"
      class="sidebar-overlay"
      @click="$emit('close')"
    ></div>

    <!-- Sidebar do Carrinho -->
    <div
      class="carrinho-sidebar"
      :class="{ 'sidebar-open': isOpen }"
    >
      <!-- Cabeçalho -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold mb-0">
          <i class="ph ph-handbag me-2"></i>Meu Carrinho
        </h5>
        <button class="btn-close-modal" @click="$emit('close')">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <!-- Controles de seleção + contador (sempre visível) -->
      <div class="selecao-bar mb-3">
        <span class="selecao-contador">
          <i class="ph ph-check-circle me-1"></i>
          {{ itensSelecionados.length }} de {{ itens.length }} {{ itensSelecionados.length === 1 ? 'item selecionado' : 'itens selecionados' }}
        </span>
        <button
          class="btn-sel"
          :class="{ 'btn-sel--ativo': todosSelecionados }"
          :disabled="itens.length === 0"
          @click="toggleTodos"
        >
          <i class="ph" :class="todosSelecionados ? 'ph-check-square' : 'ph-square'"></i>
          {{ todosSelecionados ? 'Desmarcar tudo' : 'Selecionar tudo' }}
        </button>
      </div>

      <!-- Carrinho vazio -->
      <div v-if="itens.length === 0" class="text-center py-5">
        <i
          class="ph ph-handbag"
          style="font-size: 3rem; color: var(--text-secondary)"
        ></i>
        <p class="mt-3 text-muted">O seu carrinho está vazio.</p>
      </div>

      <!-- Itens do carrinho -->
      <div v-else class="d-flex flex-column" style="height: calc(100% - 140px)">
        <div class="sidebar-itens flex-grow-1">
          <div
            v-for="item in itens"
            :key="item.itemId"
            class="sidebar-item d-flex align-items-center gap-2 py-2"
            :class="{ 'sidebar-item--selecionado': itensSelecionados.includes(item.itemId) }"
          >
            <!-- Checkbox -->
            <div class="checkbox-wrapper" @click="toggleSelecao(item.itemId)">
              <div
                class="custom-checkbox"
                :class="{ 'custom-checkbox--checked': itensSelecionados.includes(item.itemId) }"
              >
                <i class="ph ph-check" v-if="itensSelecionados.includes(item.itemId)"></i>
              </div>
            </div>

            <img
              :src="`http://localhost:8080/api/produtos/${item.produtoId}/imagem`"
              :alt="item.produtoNome"
              class="sidebar-item-img rounded-2"
            />
            <div class="flex-grow-1">
              <p class="fw-bold mb-0 small text-truncate" style="max-width: 130px">{{ item.produtoNome }}</p>
              <div class="d-flex align-items-center gap-1 flex-wrap mb-1">
                <template v-if="item.desconto > 0">
                  <span class="text-decoration-line-through text-muted" style="font-size: 0.72rem">
                    R$ {{ item.precoOriginal.toFixed(2) }}
                  </span>
                  <span class="fw-bold text-danger" style="font-size: 0.78rem">
                    R$ {{ item.precoUnitario.toFixed(2) }}
                  </span>
                  <span class="badge bg-danger rounded-pill px-1.5 py-0.5" style="font-size: 0.6rem; padding: 2px 4px; line-height: 1;">
                    -{{ item.desconto }}%
                  </span>
                </template>
                <template v-else>
                  <span class="text-muted" style="font-size: 0.78rem">
                    R$ {{ item.precoUnitario.toFixed(2) }}
                  </span>
                </template>
              </div>
              <div class="d-flex align-items-center gap-1">
                <button
                  class="btn-qty-sm"
                  @click="$emit('remover-item', item.produtoId)"
                >
                  <i class="ph ph-minus"></i>
                </button>
                <span class="fw-bold small">{{ item.quantidade }}</span>
                <button
                  class="btn-qty-sm"
                  @click="$emit('adicionar-item', item.produtoId)"
                >
                  <i class="ph ph-plus"></i>
                </button>
              </div>
            </div>
            <p class="fw-bold mb-0 small" style="color: var(--primary-color); white-space: nowrap">
              R$ {{ item.precoTotal.toFixed(2) }}
            </p>
          </div>
        </div>

        <!-- Rodapé -->
        <div class="sidebar-footer mt-auto pt-3">
          <!-- Aviso sem seleção -->
          <div v-if="nenhumSelecionado && itens.length > 0" class="aviso-selecao mb-3">
            <i class="ph ph-info me-1"></i> Selecione itens para finalizar.
          </div>

          <!-- Subtotal dos selecionados -->
          <div v-else class="d-flex justify-content-between mb-2">
            <span class="text-muted small">
              Subtotal <span class="text-muted" style="font-size:0.75rem">({{ itensSelecionados.length }} {{ itensSelecionados.length === 1 ? 'item' : 'itens' }})</span>
            </span>
            <span class="fw-bold">R$ {{ subtotalSelecionado.toFixed(2) }}</span>
          </div>

          <button
            class="btn-premium w-100 mb-2"
            :disabled="nenhumSelecionado || finalizando"
            @click="finalizarSelecionados"
          >
            <span v-if="finalizando">
              <span class="spinner-border spinner-border-sm me-1"></span>A processar...
            </span>
            <span v-else>
              Finalizar {{ itensSelecionados.length > 0 ? `(${itensSelecionados.length})` : '' }}
            </span>
          </button>

          <RouterLink
            to="/carrinho"
            class="btn-ver-carrinho w-100 text-center text-decoration-none d-block"
            @click="$emit('close')"
          >
            <i class="ph ph-list me-1"></i> Ver carrinho completo
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import api from "../../services/api";
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  isOpen: { type: Boolean, default: false },
  itens: { type: Array, default: () => [] },
  subtotal: { type: Number, default: 0 },
  usuarioId: { type: [Number, String], default: null },
});

const emit = defineEmits(["close", "adicionar-item", "remover-item", "carrinho-atualizado"]);

// ── Seleção ──────────────────────────────────────────────────────────────────
const itensSelecionados = ref([]);
const finalizando = ref(false);

// Selecionar todos ao carregar os itens (ou quando a lista mudar)
watch(
  () => props.itens,
  (novosItens) => {
    // Manter seleções existentes, adicionar novos itens como selecionados
    const idsAtuais = new Set(novosItens.map((i) => i.itemId));
    // Remover ids que não existem mais
    itensSelecionados.value = itensSelecionados.value.filter((id) => idsAtuais.has(id));
    // Adicionar novos itens que ainda não estão selecionados
    for (const item of novosItens) {
      if (!itensSelecionados.value.includes(item.itemId)) {
        itensSelecionados.value.push(item.itemId);
      }
    }
  },
  { immediate: true }
);

const todosSelecionados = computed(
  () => props.itens.length > 0 && itensSelecionados.value.length === props.itens.length
);
const nenhumSelecionado = computed(() => itensSelecionados.value.length === 0);

const itensParaCheckout = computed(() =>
  props.itens.filter((i) => itensSelecionados.value.includes(i.itemId))
);

const subtotalSelecionado = computed(() =>
  itensParaCheckout.value.reduce((acc, i) => acc + i.precoTotal, 0)
);

const toggleSelecao = (itemId) => {
  const idx = itensSelecionados.value.indexOf(itemId);
  if (idx === -1) itensSelecionados.value.push(itemId);
  else itensSelecionados.value.splice(idx, 1);
};

const toggleTodos = () => {
  if (todosSelecionados.value) {
    itensSelecionados.value = [];
  } else {
    itensSelecionados.value = props.itens.map((i) => i.itemId);
  }
};

// ── Checkout parcial ──────────────────────────────────────────────────────────
const finalizarSelecionados = async () => {
  if (!props.usuarioId || nenhumSelecionado.value) return;
  finalizando.value = true;
  try {
    const itensPayload = itensParaCheckout.value.map((item) => ({
      produtoId: item.produtoId,
      quantidade: item.quantidade,
    }));

    await api.post("/pedidos/checkout", {
      usuarioId: props.usuarioId,
      itens: itensPayload,
    });

    // Remover do carrinho os itens pedidos
    await Promise.all(
      itensParaCheckout.value.map((item) =>
        api.delete(`/carrinhos/${props.usuarioId}/remover-tudo/${item.produtoId}`)
      )
    );

    alert("Pedido realizado com sucesso!");
    emit("carrinho-atualizado");
    emit("close");

    if (todosSelecionados.value) {
      router.push("/");
    }
  } catch (error) {
    alert(error.response?.data || "Erro ao finalizar compra.");
  } finally {
    finalizando.value = false;
  }
};
</script>

<style scoped>
.carrinho-sidebar {
  position: fixed;
  top: 0;
  right: -420px;
  width: 400px;
  height: 100vh;
  background: white;
  box-shadow: -10px 0 40px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  padding: 2rem;
  transition: right 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  display: flex;
  flex-direction: column;
  text-align: left;
}

.sidebar-open {
  right: 0;
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(2px);
  z-index: 9998;
}

/* ── Barra de seleção ── */
.selecao-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 0.45rem 0.75rem;
}

.selecao-contador {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--primary-color);
}

.btn-sel {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0.3rem 0.7rem;
  border-radius: 8px;
  border: 2px solid #1a1a1a;
  background: #f5f5f7;
  color: #1a1a1a;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
  white-space: nowrap;
}
.btn-sel:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}
.btn-sel:not(:disabled):hover {
  background: #e8e8ed;
}
/* Estado "Desmarcar tudo": fundo preto, texto branco */
.btn-sel--ativo {
  background: #1a1a1a;
  color: #ffffff;
  border-color: #1a1a1a;
}
.btn-sel--ativo:not(:disabled):hover {
  background: #333;
  border-color: #333;
}

/* ── Checkbox ── */
.checkbox-wrapper {
  cursor: pointer;
  flex-shrink: 0;
}
.custom-checkbox {
  width: 18px;
  height: 18px;
  border-radius: 5px;
  border: 2px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  color: white;
  transition: all 0.15s;
}
.custom-checkbox--checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

/* ── Itens ── */
.sidebar-itens {
  overflow-y: auto;
  max-height: calc(100vh - 300px);
}

.sidebar-item {
  border-bottom: 1px solid #f1f5f9;
  border-radius: 8px;
  transition: background 0.15s;
  padding-left: 4px;
  padding-right: 4px;
}
.sidebar-item:last-child {
  border-bottom: none;
}
.sidebar-item--selecionado {
  background: color-mix(in srgb, var(--primary-color) 5%, transparent);
}

.sidebar-item-img {
  width: 54px;
  height: 54px;
  object-fit: cover;
  flex-shrink: 0;
}

/* ── Footer ── */
.sidebar-footer {
  border-top: 1px solid #f1f5f9;
}

.aviso-selecao {
  background: #fef9ec;
  border: 1px solid #f5c75e;
  border-radius: 8px;
  padding: 0.5rem 0.75rem;
  font-size: 0.8rem;
  color: #7a5c00;
}

.btn-ver-carrinho {
  display: block;
  padding: 0.5rem;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  font-size: 0.82rem;
  color: var(--text-secondary);
  transition: all 0.2s;
  background: transparent;
}
.btn-ver-carrinho:hover {
  background: #f8fafc;
  color: var(--text-primary);
}

/* ── Controles de quantidade ── */
.btn-close-modal {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s;
}
.btn-close-modal:hover {
  color: var(--text-primary);
}

.btn-qty-sm {
  width: 22px;
  height: 22px;
  border: 1px solid #e2e8f0;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 0.7rem;
  transition: all 0.2s;
}
.btn-qty-sm:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
</style>
