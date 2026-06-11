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
      <CarrinhoVazio
        v-if="carregando || !estaLogado || itens.length === 0"
        :carregando="carregando"
        :esta-logado="estaLogado"
      />


      <div v-else class="row g-4 fade-in-up">
        <div class="col-lg-8">
          <CarrinhoItens
            :itens="itens"
            :itens-selecionados="itensSelecionados"
            :todos-selecionados="todosSelecionados"
            :atualizando="atualizando"
            @toggle-selecao="toggleSelecao"
            @toggle-todos="toggleTodos"
            @adicionar="adicionarItem"
            @remover="removerItem"
          />
        </div>

        <div class="col-lg-4">
          <CarrinhoResumo
            :itens-selecionados="itensSelecionados"
            :itens-para-checkout="itensParaCheckout"
            :subtotal="subtotalSelecionado"
            :finalizando="finalizando"
            @finalizar="finalizarCompra"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";
import { showToast } from "../../services/toast";

import CarrinhoVazio from "../../components/carrinho/CarrinhoVazio.vue";
import CarrinhoItens from "../../components/carrinho/CarrinhoItens.vue";
import CarrinhoResumo from "../../components/carrinho/CarrinhoResumo.vue";

const router = useRouter();

const estaLogado = ref(false);
const carregando = ref(true);
const atualizando = ref(false);
const finalizando = ref(false);

const itens = ref([]);
const usuarioId = ref(null);
const itensSelecionados = ref([]);


const itensParaCheckout = computed(() =>
  itens.value.filter((i) => itensSelecionados.value.includes(i.itemId))
);

const subtotalSelecionado = computed(() =>
  itensParaCheckout.value.reduce((acc, i) => acc + i.precoTotal, 0)
);

const todosSelecionados = computed(
  () => itens.value.length > 0 && itensSelecionados.value.length === itens.value.length
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
    itensSelecionados.value = itens.value.map((i) => i.itemId);
  }
};


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
    itensSelecionados.value = itens.value.map((i) => i.itemId);
  } catch (error) {
    if (error.response?.status === 404) itens.value = [];
  } finally {
    carregando.value = false;
  }
};

const adicionarItem = async (produtoId) => {
  atualizando.value = true;
  try {
    const idsAntes = new Set(itens.value.map((i) => i.itemId));
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade: 1,
    });
    itens.value = resposta.data.itens;
    for (const item of itens.value) {
      if (!idsAntes.has(item.itemId) && !itensSelecionados.value.includes(item.itemId)) {
        itensSelecionados.value.push(item.itemId);
      }
    }
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : rawData?.message || "Erro ao adicionar item.";
    showToast(msg, "error");
  } finally {
    atualizando.value = false;
  }
};

const removerItem = async (produtoId) => {
  atualizando.value = true;
  try {
    const resposta = await api.delete(`/carrinhos/${usuarioId.value}/remover/${produtoId}`);
    const idsDepois = new Set(resposta.data.itens.map((i) => i.itemId));
    itensSelecionados.value = itensSelecionados.value.filter((id) => idsDepois.has(id));
    itens.value = resposta.data.itens;
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : rawData?.message || "Erro ao remover item.";
    showToast(msg, "error");
  } finally {
    atualizando.value = false;
  }
};

const finalizarCompra = async () => {
  if (itensSelecionados.value.length === 0 || !usuarioId.value) return;

  finalizando.value = true;
  try {
    const itensPayload = itensParaCheckout.value.map((item) => ({
      produtoId: item.produtoId,
      quantidade: item.quantidade,
    }));

    const resposta = await api.post("/pedidos/checkout", {
      usuarioId: usuarioId.value,
      itens: itensPayload,
    });

    localStorage.removeItem("vestebem_checkout_itens");
    router.push({ path: "/pagamento", query: { pedidoId: resposta.data.id } });
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : rawData?.message || "Não há estoque suficiente do produto.";
    showToast(msg, "error");
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

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.fade-in-up {
  animation: fadeInUp 0.6s ease forwards;
}
</style>
