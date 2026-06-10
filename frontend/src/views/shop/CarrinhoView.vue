<template>
  <div class="carrinho-wrapper">
    <nav class="navbar">
      <div class="container d-flex justify-content-between align-items-center">
        <RouterLink to="/" class="logo text-dark text-decoration-none fs-4 fw-bold">
          vesteBem.
        </RouterLink>
        <span class="text-muted" style="font-size: 0.95rem">
          <i class="ph ph-handbag me-2"></i>Meu Carrinho
        </span>
      </div>
    </nav>

    <div class="container py-5">

      <div v-if="carregando" class="text-center py-5">
        <div class="spinner-border text-dark" role="status"></div>
        <p class="mt-3 text-muted">A carregar o seu carrinho...</p>
      </div>

      <div v-else-if="!estaLogado" class="text-center py-5 fade-in-up">
        <i class="ph ph-lock" style="font-size: 4rem; color: var(--text-secondary)"></i>
        <h3 class="mt-3 fw-bold">Faça login para ver o seu carrinho</h3>
        <p class="text-muted mb-4">Você precisa estar logado para adicionar e ver produtos no carrinho.</p>
        <RouterLink to="/login" class="btn-premium px-5 py-3">Fazer Login</RouterLink>
      </div>

      <div v-else-if="itens.length === 0" class="text-center py-5 fade-in-up">
        <i class="ph ph-handbag" style="font-size: 4rem; color: var(--text-secondary)"></i>
        <h3 class="mt-3 fw-bold">O seu carrinho está vazio</h3>
        <p class="text-muted mb-4">Explore os nossos produtos e adicione algo que goste.</p>
        <RouterLink to="/" class="btn-premium px-5 py-3">Continuar a Comprar</RouterLink>
      </div>

      <div v-else class="row g-4 fade-in-up">
        <!-- Lista de Itens -->
        <div class="col-lg-8">
          <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white">

            <!-- Cabeçalho com controles de seleção -->
            <div class="d-flex justify-content-between align-items-center mb-4">
              <h4 class="fw-bold mb-0">Itens do Carrinho</h4>
              <button
                class="btn-selecao"
                :class="{ 'btn-selecao--outline': todosSelecionados }"
                @click="toggleTodos"
              >
                <i class="ph me-1" :class="todosSelecionados ? 'ph-check-square' : 'ph-square'"></i>
                {{ todosSelecionados ? 'Desmarcar tudo' : 'Selecionar tudo' }}
              </button>
            </div>

            <!-- Contagem de selecionados -->
            <div class="selecao-info mb-3">
              <i class="ph ph-check-circle me-1"></i>
              {{ itensSelecionados.length }} de {{ itens.length }} {{ itensSelecionados.length === 1 ? 'item selecionado' : 'itens selecionados' }}
            </div>

            <!-- Item Row -->
            <div
              v-for="item in itens"
              :key="item.itemId"
              class="item-row d-flex align-items-center gap-3 py-3"
              :class="{ 'item-row--selecionado': itensSelecionados.includes(item.itemId) }"
            >
              <!-- Checkbox de seleção -->
              <div class="checkbox-wrapper" @click="toggleSelecao(item.itemId)">
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
                <p class="text-muted small mb-0">
                  R$ {{ item.precoUnitario.toFixed(2) }} cada
                </p>
              </div>

              <div class="d-flex align-items-center gap-2">
                <button
                  class="btn-qty"
                  @click="removerItem(item.produtoId)"
                  :disabled="atualizando"
                >
                  <i class="ph ph-minus"></i>
                </button>
                <span class="fw-bold px-2">{{ item.quantidade }}</span>
                <button
                  class="btn-qty"
                  @click="adicionarItem(item.produtoId)"
                  :disabled="atualizando"
                >
                  <i class="ph ph-plus"></i>
                </button>
              </div>

              <div class="text-end" style="min-width: 90px">
                <p class="fw-bold mb-0" style="color: var(--primary-color)">
                  R$ {{ item.precoTotal.toFixed(2) }}
                </p>
              </div>
            </div>

          </div>
        </div>

        <!-- Resumo do Pedido -->
        <div class="col-lg-4">
          <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white sticky-summary">
            <h4 class="fw-bold mb-4">Resumo do Pedido</h4>

            <!-- Info de seleção vazia -->
            <div v-if="itensSelecionados.length === 0" class="aviso-selecao mb-4">
              <i class="ph ph-info me-2"></i>
              Selecione ao menos um item para finalizar o pedido.
            </div>

            <div v-else>
              <!-- Lista resumida dos itens selecionados -->
              <div class="itens-resumo mb-3">
                <div
                  v-for="item in itensParaCheckout"
                  :key="item.itemId"
                  class="d-flex justify-content-between align-items-center mb-2 pequeno"
                >
                  <span class="text-muted text-truncate" style="max-width: 160px">
                    {{ item.produtoNome }} <span class="badge-qty">×{{ item.quantidade }}</span>
                  </span>
                  <span class="fw-semibold">R$ {{ item.precoTotal.toFixed(2) }}</span>
                </div>
              </div>

              <hr />

              <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Subtotal</span>
                <span class="fw-bold">R$ {{ subtotalSelecionado.toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-4">
                <span class="text-muted">Frete</span>
                <span class="text-success fw-bold">Grátis</span>
              </div>

              <hr />

              <div class="d-flex justify-content-between mb-4">
                <span class="fw-bold fs-5">Total</span>
                <span class="fw-bold fs-5" style="color: var(--primary-color)">
                  R$ {{ subtotalSelecionado.toFixed(2) }}
                </span>
              </div>
            </div>

            <button
              class="btn-premium w-100"
              :disabled="finalizando || itensSelecionados.length === 0"
              @click="finalizarCompra"
            >
              <span v-if="finalizando">
                <span class="spinner-border spinner-border-sm me-2"></span>A processar...
              </span>
              <span v-else>
                Finalizar {{ itensSelecionados.length > 0 ? `(${itensSelecionados.length} ${itensSelecionados.length === 1 ? 'item' : 'itens'})` : '' }}
              </span>
            </button>

            <RouterLink
              to="/"
              class="d-block text-center mt-3 text-muted text-decoration-none"
              style="font-size: 0.9rem"
            >
              <i class="ph ph-arrow-left me-1"></i> Continuar a Comprar
            </RouterLink>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";

const router = useRouter();

const estaLogado = ref(false);
const carregando = ref(true);
const atualizando = ref(false);
const finalizando = ref(false);

const itens = ref([]);
const usuarioId = ref(null);

// IDs dos itemId selecionados para o pedido
const itensSelecionados = ref([]);

// Computed: itens completos selecionados
const itensParaCheckout = computed(() =>
  itens.value.filter((i) => itensSelecionados.value.includes(i.itemId))
);

// Subtotal apenas dos selecionados
const subtotalSelecionado = computed(() =>
  itensParaCheckout.value.reduce((acc, i) => acc + i.precoTotal, 0)
);

const todosSelecionados = computed(
  () =>
    itens.value.length > 0 &&
    itensSelecionados.value.length === itens.value.length
);
const nenhumSelecionado = computed(() => itensSelecionados.value.length === 0);

// ─── Helpers de Seleção ───────────────────────────────────────────────────────
const toggleSelecao = (itemId) => {
  const idx = itensSelecionados.value.indexOf(itemId);
  if (idx === -1) {
    itensSelecionados.value.push(itemId);
  } else {
    itensSelecionados.value.splice(idx, 1);
  }
};

const toggleTodos = () => {
  if (todosSelecionados.value) {
    itensSelecionados.value = [];
  } else {
    itensSelecionados.value = itens.value.map((i) => i.itemId);
  }
};


// ─── API Calls ────────────────────────────────────────────────────────────────
const obterUsuarioId = () => {
  const token = localStorage.getItem("token_vestebem");
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.usuarioId || payload.id || null;
  } catch {
    return null;
  }
};

const carregarCarrinho = async () => {
  carregando.value = true;
  try {
    const resposta = await api.get(`/carrinhos/${usuarioId.value}`);
    itens.value = resposta.data.itens;
    // Ao carregar, selecionar todos por padrão
    itensSelecionados.value = itens.value.map((i) => i.itemId);
  } catch (error) {
    if (error.response?.status === 404) {
      itens.value = [];
    }
  } finally {
    carregando.value = false;
  }
};

const adicionarItem = async (produtoId) => {
  atualizando.value = true;
  try {
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade: 1,
    });
    const idsAntes = new Set(itens.value.map((i) => i.itemId));
    itens.value = resposta.data.itens;
    // Manter seleções existentes; adicionar novos itens como selecionados
    for (const item of itens.value) {
      if (!idsAntes.has(item.itemId) && !itensSelecionados.value.includes(item.itemId)) {
        itensSelecionados.value.push(item.itemId);
      }
    }
  } catch (error) {
    alert(error.response?.data || "Erro ao adicionar item.");
  } finally {
    atualizando.value = false;
  }
};

const removerItem = async (produtoId) => {
  atualizando.value = true;
  try {
    const resposta = await api.delete(
      `/carrinhos/${usuarioId.value}/remover/${produtoId}`
    );
    // Guardar IDs que sumiram para remover da seleção
    const idsDepois = new Set(resposta.data.itens.map((i) => i.itemId));
    itensSelecionados.value = itensSelecionados.value.filter((id) =>
      idsDepois.has(id)
    );
    itens.value = resposta.data.itens;
  } catch (error) {
    alert(error.response?.data || "Erro ao remover item.");
  } finally {
    atualizando.value = false;
  }
};

const finalizarCompra = async () => {
  if (itensSelecionados.value.length === 0) return;

  finalizando.value = true;
  try {
    // Montar payload apenas com os itens selecionados
    const itensPayload = itensParaCheckout.value.map((item) => ({
      produtoId: item.produtoId,
      quantidade: item.quantidade,
    }));

    await api.post("/pedidos/checkout", {
      usuarioId: usuarioId.value,
      itens: itensPayload,
    });

    alert("Pedido realizado com sucesso!");

    // Remover do carrinho apenas os itens que foram pedidos (em paralelo)
    await Promise.all(
      itensParaCheckout.value.map((item) =>
        api.delete(`/carrinhos/${usuarioId.value}/remover-tudo/${item.produtoId}`)
      )
    );

    // Se o carrinho ficou vazio, ir para home; caso contrário, recarregar
    if (todosSelecionados.value) {
      router.push("/");
    } else {
      await carregarCarrinho();
    }
  } catch (error) {
    alert(error.response?.data || "Erro ao finalizar compra.");
  } finally {
    finalizando.value = false;
  }
};

onMounted(() => {
  const token = localStorage.getItem("token_vestebem");
  if (token) {
    estaLogado.value = true;
    usuarioId.value = obterUsuarioId();
    if (usuarioId.value) {
      carregarCarrinho();
    } else {
      carregando.value = false;
    }
  } else {
    carregando.value = false;
  }
});
</script>

<style scoped>
.carrinho-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color);
}

.navbar {
  padding: 1.5rem 0;
  background: white;
  box-shadow: 0 1px 0 #e2e8f0;
}

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
/* Estado "Desmarcar tudo": fundo preto, texto branco */
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

/* ── Checkbox custom ── */
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

/* ── Resumo ── */
.sticky-summary {
  position: sticky;
  top: 90px;
}

.aviso-selecao {
  background: #fef9ec;
  border: 1px solid #f5c75e;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-size: 0.85rem;
  color: #7a5c00;
}

.itens-resumo {
  max-height: 180px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.pequeno {
  font-size: 0.85rem;
}

.badge-qty {
  display: inline-block;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 6px;
  padding: 0 5px;
  font-size: 0.75rem;
  font-weight: 700;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.fade-in-up {
  animation: fadeInUp 0.6s ease forwards;
}
</style>
