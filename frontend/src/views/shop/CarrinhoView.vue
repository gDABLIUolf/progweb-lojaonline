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
        <div class="col-lg-8">
          <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white">
            <h4 class="fw-bold mb-4">Itens do Carrinho</h4>

            <div
              v-for="item in itens"
              :key="item.itemId"
              class="item-row d-flex align-items-center gap-3 py-3"
            >
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

        <div class="col-lg-4">
          <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white">
            <h4 class="fw-bold mb-4">Resumo</h4>

            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Subtotal</span>
              <span class="fw-bold">R$ {{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-4">
              <span class="text-muted">Frete</span>
              <span class="text-success fw-bold">Grátis</span>
            </div>

            <hr />

            <div class="d-flex justify-content-between mb-4">
              <span class="fw-bold fs-5">Total</span>
              <span class="fw-bold fs-5" style="color: var(--primary-color)">
                R$ {{ subtotal.toFixed(2) }}
              </span>
            </div>

            <button
              class="btn-premium w-100"
              :disabled="finalizando"
              @click="finalizarCompra"
            >
              {{ finalizando ? "A processar..." : "Finalizar Compra" }}
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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";

const router = useRouter();

const estaLogado = ref(false);
const carregando = ref(true);
const atualizando = ref(false);
const finalizando = ref(false);

const itens = ref([]);
const subtotal = ref(0);
const usuarioId = ref(null);

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
    subtotal.value = resposta.data.subtotal;
  } catch (error) {
    if (error.response?.status === 404) {
      itens.value = [];
      subtotal.value = 0;
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
    itens.value = resposta.data.itens;
    subtotal.value = resposta.data.subtotal;
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
    itens.value = resposta.data.itens;
    subtotal.value = resposta.data.subtotal;
  } catch (error) {
    alert(error.response?.data || "Erro ao remover item.");
  } finally {
    atualizando.value = false;
  }
};

const finalizarCompra = async () => {
  finalizando.value = true;
  try {
    await api.post(`/pedidos/carrinho/${usuarioId.value}`);
    alert("Pedido realizado com sucesso!");
    router.push("/");
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

.item-row {
  border-bottom: 1px solid #f1f5f9;
}

.item-row:last-child {
  border-bottom: none;
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

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.fade-in-up {
  animation: fadeInUp 0.6s ease forwards;
}
</style>
