<template>
  <div class="produto-detalhes-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <div class="container py-5">
      <div v-if="carregando" class="text-center py-5">
        <div class="spinner-border text-dark" role="status"></div>
        <p class="mt-3 text-muted">A carregar detalhes do produto...</p>
      </div>

      <div v-else-if="!produto" class="text-center py-5">
        <i class="ph ph-warning-circle fs-1 text-danger mb-3"></i>
        <h3 class="fw-bold">Produto não encontrado</h3>
        <RouterLink to="/" class="btn btn-dark mt-3">Voltar ao Catálogo</RouterLink>
      </div>

      <div v-else class="row g-5">
        <!-- Coluna da Esquerda: Galeria de Imagens -->
        <div class="col-lg-6">
          <div class="galeria-container">
            <!-- Imagem Principal Ativa -->
            <div class="imagem-principal-wrapper position-relative rounded-4 overflow-hidden shadow-sm bg-light">
              <img
                v-if="imagemPrincipalUrl"
                :src="imagemPrincipalUrl"
                :alt="produto.nome"
                class="imagem-principal"
              />
              <div
                v-else
                class="imagem-principal d-flex align-items-center justify-content-center text-muted"
              >
                Sem Imagem
              </div>

              <!-- Setas Prev/Next se houver mais de uma foto -->
              <template v-if="produto.imagensIds?.length > 1">
                <button class="btn-seta seta-esquerda" @click="anteriorImagem" title="Anterior">
                  <i class="ph ph-caret-left"></i>
                </button>
                <button class="btn-seta seta-direita" @click="proximaImagem" title="Próxima">
                  <i class="ph ph-caret-right"></i>
                </button>
              </template>
            </div>

            <!-- Miniaturas (Thumbnails) -->
            <div v-if="produto.imagensIds?.length > 1" class="miniaturas-row d-flex gap-2 mt-3 overflow-x-auto">
              <button
                v-for="(imgId, idx) in produto.imagensIds"
                :key="imgId"
                class="miniatura-btn rounded-3 overflow-hidden"
                :class="{ 'miniatura-ativa': indiceImagemAtiva === idx }"
                @click="indiceImagemAtiva = idx"
              >
                <img
                  :src="`http://localhost:8080/api/produtos/imagem/${imgId}`"
                  :alt="`Miniatura ${idx + 1}`"
                  class="miniatura-img"
                />
              </button>
            </div>
          </div>
        </div>

        <!-- Coluna da Direita: Informações do Produto -->
        <div class="col-lg-6 d-flex flex-column text-start">
          <div class="mb-3 d-flex flex-wrap gap-2">
            <span
              v-for="cat in produto.categoriasNomes"
              :key="cat"
              class="badge bg-dark px-3 py-2 fw-normal"
            >
              {{ cat }}
            </span>
          </div>

          <h1 class="fw-bold mb-3 display-5 text-dark">{{ produto.nome }}</h1>

          <!-- Média de Avaliações (Estrelas) -->
          <div class="d-flex align-items-center mb-4">
            <div class="d-flex align-items-center" style="gap: 3px;">
              <span
                v-for="n in 5"
                :key="n"
                style="position: relative; display: inline-flex; width: 20px; height: 20px; flex-shrink: 0;"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" style="width:20px;height:20px;display:block;">
                  <path
                    d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                    fill="#e0e0e0"
                  />
                </svg>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 256 256"
                  style="position:absolute;top:0;left:0;width:20px;height:20px;display:block;"
                  :style="{ clipPath: `inset(0 ${100 - calcularPorcentagem(produto.mediaAvaliacoes || 0, n)}% 0 0)` }"
                >
                  <path
                    d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                    fill="#ffc107"
                  />
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
              <span class="preco-antigo text-decoration-line-through text-muted fs-4">R$ {{ precoFormatado }}</span>
              <span class="preco-valor display-6 fw-bold text-danger">R$ {{ precoPromocionalFormatado }}</span>
              <span class="badge bg-danger rounded-pill px-3 py-2 fw-normal fs-6">-{{ produto.desconto }}%</span>
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

          <!-- Seletor de Quantidade e Botão Adicionar ao Carrinho -->
          <div class="d-flex align-items-center gap-3 mt-auto">
            <div class="d-flex align-items-center border rounded-pill px-2 py-1" style="background: #f8fafc;">
              <button class="btn-qty" @click="decrementarQtd">
                <i class="ph ph-minus"></i>
              </button>
              <span class="fw-bold px-3" style="min-width: 40px; text-align: center;">
                {{ quantidadeSelecionada }}
              </span>
              <button class="btn-qty" @click="incrementarQtd">
                <i class="ph ph-plus"></i>
              </button>
            </div>

            <button class="btn btn-dark rounded-pill px-5 py-3 fw-bold flex-grow-1" @click="adicionarProdutoSelecionado">
              Adicionar ao carrinho
            </button>
          </div>
        </div>
      </div>

      <!-- Seção de Avaliações dos Clientes -->
      <div v-if="produto" class="row mt-5 pt-5 border-top text-start">
        <div class="col-12">
          <h3 class="fw-bold mb-4">Avaliações dos Clientes</h3>

          <div v-if="!produto.avaliacoes?.length" class="text-muted py-4">
            <i class="ph ph-chat-centered-dots fs-1 mb-2"></i>
            <p>Este produto ainda não possui avaliações. Seja o primeiro a avaliar!</p>
          </div>

          <div v-else class="d-flex flex-column gap-4">
            <div
              v-for="avaliacao in produto.avaliacoes"
              :key="avaliacao.id"
              class="p-4 rounded-4 bg-light shadow-sm"
            >
              <div class="d-flex justify-content-between align-items-start mb-2">
                <div>
                  <h6 class="fw-bold mb-1">{{ avaliacao.nomeUsuario }}</h6>
                  <span class="text-muted small">{{ formatarData(avaliacao.dataAvaliacao) }}</span>
                </div>

                <!-- Nota da Avaliação individual -->
                <div class="d-flex align-items-center" style="gap: 2px;">
                  <span
                    v-for="star in 5"
                    :key="star"
                    style="position: relative; display: inline-flex; width: 16px; height: 16px;"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" style="width:16px;height:16px;display:block;">
                      <path
                        d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                        fill="#e0e0e0"
                      />
                    </svg>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 256 256"
                      style="position:absolute;top:0;left:0;width:16px;height:16px;display:block;"
                      :style="{ clipPath: `inset(0 ${100 - (avaliacao.nota >= star ? 100 : (avaliacao.nota > star - 1 ? (avaliacao.nota - (star - 1)) * 100 : 0))}% 0 0)` }"
                    >
                      <path
                        d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                        fill="#ffc107"
                      />
                    </svg>
                  </span>
                </div>
              </div>
              <p class="text-secondary mb-0 mt-2">{{ avaliacao.comentario }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Botão flutuante para abrir o carrinho -->
    <button
      class="btn-carrinho-flutuante"
      @click="sidebarAberta = true"
      title="Ver carrinho"
    >
      <i class="ph ph-handbag"></i>
      <span v-if="quantidadeCarrinho > 0" class="flutuante-count">
        {{ quantidadeCarrinho }}
      </span>
    </button>

    <!-- Sidebar do Carrinho -->
    <CarrinhoSidebar
      :is-open="sidebarAberta"
      :itens="itensCarrinho"
      :subtotal="subtotalCarrinho"
      :usuario-id="usuarioId"
      @close="sidebarAberta = false"
      @adicionar-item="adicionarItemSidebar"
      @remover-item="removerItemSidebar"
      @carrinho-atualizado="carregarCarrinho"
    />

    <!-- Modal de login necessário -->
    <div
      v-if="modalLoginAberto"
      class="modal-overlay"
      @click.self="modalLoginAberto = false"
    >
      <div class="modal-content fade-in-up text-center">
        <i
          class="ph ph-lock"
          style="font-size: 3rem; color: var(--text-secondary)"
        ></i>
        <h4 class="fw-bold mt-3">Faça login para comprar</h4>
        <p class="text-muted mb-4">
          Você precisa estar logado para adicionar produtos ao carrinho.
        </p>
        <RouterLink
          to="/login"
          class="btn-premium w-100 text-decoration-none d-block mb-3"
        >
          Fazer Login
        </RouterLink>
        <button
          class="btn btn-link text-muted text-decoration-none"
          @click="modalLoginAberto = false"
        >
          Continuar navegando
        </button>
      </div>
    </div>

    <!-- Rodapé Premium -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter, onBeforeRouteLeave } from "vue-router";
import Navbar from "../../components/layout/Navbar.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import Footer from "../../components/layout/Footer.vue";
import api from "../../services/api";
import { formatarData } from "../../utils/date.js";
import { showToast } from "../../services/toast";

const route = useRoute();
const router = useRouter();

const carregando = ref(true);
const produto = ref(null);

const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");

const usuarioId = ref(null);
const itensCarrinho = ref([]);
const subtotalCarrinho = ref(0);
const sidebarAberta = ref(false);
const modalLoginAberto = ref(false);

const indiceImagemAtiva = ref(0);
const quantidadeSelecionada = ref(1);

const quantidadeCarrinho = computed(() => {
  return itensCarrinho.value.length;
});

const precoFormatado = computed(() => {
  if (!produto.value) return "0,00";
  return Number(produto.value.preco).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
  });
});

const precoPromocionalFormatado = computed(() => {
  if (!produto.value || !produto.value.desconto) return "";
  const precoOriginal = Number(produto.value.preco);
  const precoFinal = precoOriginal * (1 - produto.value.desconto / 100);
  return precoFinal.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
});

const imagemPrincipalUrl = computed(() => {
  if (!produto.value || !produto.value.imagensIds || produto.value.imagensIds.length === 0) {
    return null;
  }
  const activeId = produto.value.imagensIds[indiceImagemAtiva.value];
  return `http://localhost:8080/api/produtos/imagem/${activeId}`;
});

const decodificarJWT = (token) => {
  try {
    const payload = token.split(".")[1];
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
};

const carregarUsuario = async () => {
  const token = localStorage.getItem("token_vestebem");
  if (!token) return;

  estaLogado.value = true;
  const dadosToken = decodificarJWT(token);

  if (!dadosToken) return;

  usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

  const cachedName = localStorage.getItem("nome_usuario_vestebem");
  if (cachedName) {
    nomeUsuario.value = cachedName;
  } else if (dadosToken.sub) {
    nomeUsuario.value = dadosToken.sub.split("@")[0];
  }

  const permissao = dadosToken.role || "";
  isAdmin.value = permissao.toUpperCase() === "ADMIN";

  // Busca o nome real no backend para atualizar o cache
  if (usuarioId.value) {
    try {
      const resposta = await api.get(`/usuarios/${usuarioId.value}`);
      const nomeCompleto = resposta.data.nome || "";
      const primeiroNome = nomeCompleto.split(" ")[0];
      nomeUsuario.value = primeiroNome;
      localStorage.setItem("nome_usuario_vestebem", primeiroNome);
    } catch {
      // Mantém fallback caso a busca falhe
    }
  }
};

const carregarCarrinho = async () => {
  if (!usuarioId.value) return;
  try {
    const resposta = await api.get(`/carrinhos/${usuarioId.value}`);
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch {
    itensCarrinho.value = [];
    subtotalCarrinho.value = 0;
  }
};

const carregarProduto = async () => {
  carregando.value = true;
  try {
    const response = await api.get(`/produtos/${route.params.id}`);
    produto.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar detalhes do produto:", error);
  } finally {
    carregando.value = false;
  }
};

const fazerLogout = () => {
  localStorage.removeItem("token_vestebem");
  localStorage.removeItem("nome_usuario_vestebem");

  estaLogado.value = false;
  isAdmin.value = false;
  nomeUsuario.value = "";
  usuarioId.value = null;
  itensCarrinho.value = [];
  subtotalCarrinho.value = 0;

  router.push("/");
};

const adicionarAoCarrinho = async (produtoId, quantidade) => {
  if (!estaLogado.value) {
    modalLoginAberto.value = true;
    return;
  }

  try {
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade,
    });
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
    sidebarAberta.value = true;
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao adicionar ao carrinho.");
    showToast(msg, "error");
  }
};

const adicionarProdutoSelecionado = () => {
  if (!produto.value) return;
  adicionarAoCarrinho(produto.value.id, quantidadeSelecionada.value);
};

const removerItemSidebar = async (produtoId) => {
  try {
    const resposta = await api.delete(
      `/carrinhos/${usuarioId.value}/remover/${produtoId}`
    );
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao remover item.");
    showToast(msg, "error");
  }
};

const adicionarItemSidebar = async (produtoId) => {
  try {
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade: 1,
    });
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao adicionar item.");
    showToast(msg, "error");
  }
};

const calcularPorcentagem = (media, posicao) => {
  const m = parseFloat(media);
  const preenchimento = m - (posicao - 1);
  if (preenchimento >= 1) return 100;
  if (preenchimento <= 0) return 0;
  return Math.round(preenchimento * 100);
};


const incrementarQtd = () => {
  quantidadeSelecionada.value++;
};

const decrementarQtd = () => {
  if (quantidadeSelecionada.value > 1) {
    quantidadeSelecionada.value--;
  }
};

const anteriorImagem = () => {
  if (!produto.value || !produto.value.imagensIds) return;
  const len = produto.value.imagensIds.length;
  indiceImagemAtiva.value = (indiceImagemAtiva.value - 1 + len) % len;
};

const proximaImagem = () => {
  if (!produto.value || !produto.value.imagensIds) return;
  const len = produto.value.imagensIds.length;
  indiceImagemAtiva.value = (indiceImagemAtiva.value + 1) % len;
};

onMounted(async () => {
  await carregarUsuario();
  if (usuarioId.value) {
    await carregarCarrinho();
  }
  await carregarProduto();
});

onBeforeRouteLeave((to, from) => {
  sessionStorage.setItem("is_returning", "true");
});
</script>

<style scoped>
.produto-detalhes-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color);
}

.galeria-container {
  display: flex;
  flex-direction: column;
}

.imagem-principal-wrapper {
  aspect-ratio: 1/1;
  width: 100%;
}

.imagem-principal {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.btn-seta {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.85);
  border: none;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  z-index: 10;
}

.btn-seta:hover {
  background: white;
  transform: translateY(-50%) scale(1.05);
}

.seta-esquerda {
  left: 1.5rem;
}

.seta-direita {
  right: 1.5rem;
}

.miniatura-btn {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border: 2px solid transparent;
  background: transparent;
  padding: 0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.miniatura-ativa {
  border-color: var(--primary-color);
}

.miniatura-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preco-valor {
  color: var(--primary-color);
}

.preco-antigo {
  color: var(--text-secondary, #86868b);
}

.text-danger {
  color: #e53e3e !important;
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
}

.btn-qty:hover {
  opacity: 0.6;
}

.btn-carrinho-flutuante {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 12px 0 0 12px;
  width: 48px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  cursor: pointer;
  z-index: 9997;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  transition: width 0.2s ease;
}

.btn-carrinho-flutuante:hover {
  width: 56px;
}

.flutuante-count {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #e53e3e;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.modal-content {
  background: var(--bg-color);
  width: 100%;
  max-width: 450px;
  border-radius: var(--radius-lg);
  padding: 2.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);
}
</style>
