<template>
  <div class="home-wrapper">
    <nav class="navbar">
      <div class="container d-flex justify-content-between align-items-center">
        <a href="#" class="logo text-dark text-decoration-none fs-4 fw-bold"
          >VesteBem.</a
        >

        <div class="nav-icons d-flex gap-3 align-items-center">
          <span
            v-if="isAdmin"
            class="badge bg-primary rounded-pill px-3 py-2 fw-normal"
          >
            <i class="ph ph-shield-check me-1"></i> Admin
          </span>

          <a href="#" title="Pesquisar" class="text-dark fs-4">
            <i class="ph ph-magnifying-glass"></i>
          </a>

          <div v-if="estaLogado" class="d-flex align-items-center gap-2">
            <span class="text-dark fw-medium" style="font-size: 0.95rem"
              >Olá, {{ nomeUsuario }}</span
            >
            <a
              @click.prevent="fazerLogout"
              title="Sair"
              class="text-dark fs-4 ms-2"
              style="cursor: pointer"
            >
              <i class="ph ph-sign-out"></i>
            </a>
          </div>

          <RouterLink
            v-else
            to="/login"
            title="Minha Conta"
            class="text-dark fs-4"
          >
            <i class="ph ph-user"></i>
          </RouterLink>

          <a href="#" title="Carrinho" class="text-dark fs-4 position-relative">
            <i class="ph ph-handbag"></i>
            <span class="cart-count">2</span>
          </a>
        </div>
      </div>
    </nav>

    <header class="hero">
      <div class="container">
        <div class="hero-grid">
          <div class="hero-text fade-in-up">
            <h1>O Essencial, Elevado.</h1>
            <p>
              Descubra a nova coleção de peças atemporais feitas com algodão
              pima e tecidos sustentáveis. Um toque suave e um caimento
              impecável.
            </p>
            <button class="btn-premium mt-3">Comprar Coleção</button>
          </div>

          <div class="hero-img-wrap fade-in-up" style="transition-delay: 0.2s">
            <img
              src="../../assets/img/hero_minimal_fashion_1780402716026.png"
              alt="VesteBem Minimal Fashion"
              class="hero-img"
            />
          </div>
        </div>
      </div>
    </header>

    <section v-if="isAdmin" class="admin-panel py-5 fade-in-up">
      <div class="container">
        <div
          class="p-4 rounded-4 shadow-sm bg-white"
          style="border-left: 5px solid var(--primary-color)"
        >
          <div class="d-flex align-items-center mb-3">
            <i
              class="ph ph-wrench fs-2 me-3"
              style="color: var(--primary-color)"
            ></i>
            <h2 class="mb-0 fs-4 fw-bold">Gestão do Catálogo</h2>
          </div>
          <p class="text-muted mb-4">
            Adicione novas coleções e gerencie o estoque da loja.
          </p>

          <div class="d-flex gap-3 flex-wrap">
            <button
              @click="abrirModalCategoria"
              class="btn-premium d-flex align-items-center gap-2"
            >
              <i class="ph ph-tag fs-5"></i> Nova Categoria
            </button>
            <button
              @click="abrirModalProduto"
              class="btn-premium d-flex align-items-center gap-2"
              style="background-color: var(--text-secondary)"
            >
              <i class="ph ph-t-shirt fs-5"></i> Novo Produto
            </button>
          </div>
        </div>
      </div>
    </section>
    <div v-if="modalCategoriaAberta" class="modal-overlay">
      <div class="modal-content fade-in-up">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="fw-bold mb-0">Nova Categoria</h3>
          <button @click="fecharModalCategoria" class="btn-close-modal">
            <i class="ph ph-x"></i>
          </button>
        </div>

        <form @submit.prevent="salvarCategoria">
          <div class="mb-3">
            <label class="form-label text-muted small fw-bold"
              >NOME DA CATEGORIA</label
            >
            <input
              type="text"
              v-model="novaCategoria.nome"
              class="form-control-premium"
              placeholder="Ex: Moda Inverno"
              required
            />
          </div>

          <div class="mb-4">
            <label class="form-label text-muted small fw-bold"
              >IMAGEM DE DESTAQUE</label
            >
            <input
              type="file"
              @change="capturarImagem"
              class="form-control-premium"
              accept="image/png, image/jpeg, image/webp"
              required
            />
          </div>

          <div
            v-if="erroModal"
            class="alert alert-danger py-2 mb-3"
            style="font-size: 0.9rem"
          >
            {{ erroModal }}
          </div>

          <button type="submit" class="btn-premium w-100" :disabled="salvando">
            {{ salvando ? "A salvar..." : "Salvar Categoria" }}
          </button>
        </form>
      </div>
    </div>

    <div v-if="modalProdutoAberta" class="modal-overlay">
      <div
        class="modal-content fade-in-up"
        style="max-width: 550px; max-height: 90vh; overflow-y: auto"
      >
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="fw-bold mb-0">Novo Produto</h3>
          <button @click="fecharModalProduto" class="btn-close-modal">
            <i class="ph ph-x"></i>
          </button>
        </div>

        <form @submit.prevent="salvarProduto">
          <div class="mb-3">
            <label class="form-label text-muted small fw-bold"
              >NOME DO PRODUTO</label
            >
            <input
              type="text"
              v-model="novoProduto.nome"
              class="form-control-premium"
              required
            />
          </div>

          <div class="mb-3">
            <label class="form-label text-muted small fw-bold">DESCRIÇÃO</label>
            <textarea
              v-model="novoProduto.descricao"
              class="form-control-premium"
              rows="3"
              required
            ></textarea>
          </div>

          <div class="row mb-3">
            <div class="col-6">
              <label class="form-label text-muted small fw-bold"
                >PREÇO (R$)</label
              >
              <input
                type="number"
                step="0.01"
                v-model="novoProduto.preco"
                class="form-control-premium"
                required
              />
            </div>
            <div class="col-6">
              <label class="form-label text-muted small fw-bold">ESTOQUE</label>
              <input
                type="number"
                v-model="novoProduto.quantidadeEstoque"
                class="form-control-premium"
                required
              />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label text-muted small fw-bold"
              >CATEGORIAS</label
            >
            <div
              class="d-flex flex-wrap gap-3 p-3 rounded"
              style="background: #f8fafc; border: 1px solid #e2e8f0"
            >
              <div class="form-check" v-for="cat in categorias" :key="cat.id">
                <input
                  class="form-check-input"
                  type="checkbox"
                  :value="cat.id"
                  :id="'cat-' + cat.id"
                  v-model="novoProduto.categoriasIds"
                />
                <label class="form-check-label" :for="'cat-' + cat.id">
                  {{ cat.nome }}
                </label>
              </div>
            </div>
            <small v-if="categorias.length === 0" class="text-danger"
              >Cadastre uma categoria primeiro!</small
            >
          </div>

          <div class="mb-4">
            <label class="form-label text-muted small fw-bold"
              >IMAGEM DO PRODUTO</label
            >
            <input
              type="file"
              @change="capturarImagemProduto"
              class="form-control-premium"
              accept="image/*"
            />
          </div>

          <div
            v-if="erroModalProduto"
            class="alert alert-danger py-2 mb-3"
            style="font-size: 0.9rem"
          >
            {{ erroModalProduto }}
          </div>

          <button
            type="submit"
            class="btn-premium w-100"
            :disabled="
              salvandoProduto || novoProduto.categoriasIds.length === 0
            "
          >
            {{ salvandoProduto ? "A salvar..." : "Salvar Produto" }}
          </button>
        </form>
      </div>
    </div>
    <section class="categories py-5">
      <div class="container text-center">
        <h2 class="mb-5">Explore por Categoria</h2>

        <p v-if="categorias.length === 0" class="text-muted mb-5">
          Nenhuma categoria cadastrada ainda.
        </p>

        <div class="row g-4">
          <div
            class="col-md-4"
            v-for="categoria in categorias"
            :key="categoria.id"
          >
            <div class="cat-card p-4 rounded-4 shadow-sm bg-white">
              <img
                :src="`http://localhost:8080/api/categorias/${categoria.id}/imagem`"
                :alt="categoria.nome"
                class="hero-img"
              />
              <h3 class="mt-3">{{ categoria.nome }}</h3>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="produtos py-5 bg-light fade-in-up">
      <div class="container text-center">
        <h2 class="mb-5">Nossos Produtos</h2>
        
        <p v-if="produtos.length === 0" class="text-muted mb-5">
          Nenhum produto disponível no momento.
        </p>

        <div class="row g-4">
          <div class="col-md-3" v-for="produto in produtos" :key="produto.id">
            <div class="card h-100 shadow-sm border-0" style="transition: transform 0.2s;">
              <img 
                :src="`http://localhost:8080/api/produtos/${produto.id}/imagem`" 
                class="card-img-top" 
                :alt="produto.nome"
                style="height: 250px; object-fit: cover;"
              >
              <div class="card-body d-flex flex-column text-start">
                <h5 class="card-title fw-bold mb-1">{{ produto.nome }}</h5>
                <p class="card-text text-muted small mb-2 text-truncate">{{ produto.descricao }}</p>
                
                <p class="fs-5 fw-bold mb-1" style="color: var(--primary-color)">R$ {{ produto.preco.toFixed(2) }}</p>
                
                <div class="mb-3 d-flex align-items-center">
                  <div class="d-flex align-items-center" style="gap: 3px;">
                    <span v-for="n in 5" :key="n"
                      style="position: relative; display: inline-flex; width: 18px; height: 18px; flex-shrink: 0;">

                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256"
                        style="width:18px;height:18px;display:block;">
                        <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                          fill="#e0e0e0"/>
                      </svg>

                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256"
                        style="position:absolute;top:0;left:0;width:18px;height:18px;display:block;"
                        :style="{ clipPath: `inset(0 ${100 - calcularPorcentagem(produto.mediaAvaliacoes || 0, n)}% 0 0)` }">
                        <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z"
                          fill="#ffc107"/>
                      </svg>

                    </span>
                  </div>

                  <span class="text-muted ms-2 small fw-bold">
                    {{ (produto.mediaAvaliacoes || 0).toFixed(1).replace('.', ',') }}
                    <span class="fw-normal">({{ produto.avaliacoes?.length || 0 }})</span>
                  </span>
                </div>

                <button class="btn-premium w-100 mt-auto py-2" style="font-size: 0.9rem;">
                  Adicionar ao Carrinho
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api, { listarProdutos } from "../../services/api";

const router = useRouter();
const produtos = ref([]);

// Variáveis de Estado da Interface
const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");

// Função para extrair os dados do JWT
const decodificarJWT = (token) => {
  try {
    const payload = token.split(".")[1];
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

// Ao carregar a página
onMounted(() => {
  const token = localStorage.getItem("token_vestebem");

  if (token) {
    estaLogado.value = true;
    const dadosToken = decodificarJWT(token);

    if (dadosToken) {
      if (dadosToken.sub) {
        nomeUsuario.value = dadosToken.sub.split("@")[0];
      }

      const roles = JSON.stringify(dadosToken).toUpperCase();
      if (roles.includes("ADMIN") || roles.includes("ROLE_ADMIN")) {
        isAdmin.value = true;
      }
    }
  }
  carregarCategorias();
  carregarProdutos();
});

// Ação de Sair
const fazerLogout = () => {
  localStorage.removeItem("token_vestebem");
  estaLogado.value = false;
  isAdmin.value = false;
  nomeUsuario.value = "";
  router.go(); // Dá um refresh na página para garantir que tudo foi limpo
};

const modalCategoriaAberta = ref(false);
const salvando = ref(false);
const erroModal = ref("");

const novaCategoria = ref({ nome: "" });
const arquivoImagem = ref(null);

const abrirModalCategoria = () => {
  modalCategoriaAberta.value = true;
  erroModal.value = "";
};

const fecharModalCategoria = () => {
  modalCategoriaAberta.value = false;
  novaCategoria.value.nome = "";
  arquivoImagem.value = null;
};

const capturarImagem = (event) => {
  arquivoImagem.value = event.target.files[0];
};

const salvarCategoria = async () => {
  if (!arquivoImagem.value) {
    erroModal.value = "Por favor, selecione uma imagem.";
    return;
  }

  salvando.value = true;
  erroModal.value = "";

  try {
    const formData = new FormData();

    const jsonBlob = new Blob(
      [JSON.stringify({ nome: novaCategoria.value.nome })],
      {
        type: "application/json",
      },
    );
    formData.append("dados", jsonBlob);

    formData.append("imagem", arquivoImagem.value);

    await api.post("/categorias", formData);

    alert("Categoria cadastrada com sucesso!");
    fecharModalCategoria();
    carregarCategorias();
  } catch (error) {
    console.error("Erro ao salvar categoria:", error);
    erroModal.value =
      "Erro ao salvar. Verifique se o tamanho da imagem é menor que 5MB e se tem permissão.";
  } finally {
    salvando.value = false;
  }
};

const modalProdutoAberta = ref(false);
const salvandoProduto = ref(false);
const erroModalProduto = ref("");

const categorias = ref([]);

const carregarCategorias = async () => {
  try {
    const resposta = await api.get("/categorias");
    categorias.value = resposta.data;
  } catch (error) {
    console.error("Erro ao buscar categorias:", error);
  }
};

const carregarProdutos = async () => {
  try {
    const resposta = await listarProdutos();
    produtos.value = resposta.data;
    console.log(produtos.value)
  } catch (error) {
    console.error("Erro ao buscar produtos:", error);
  }
};

const calcularPorcentagem = (media, posicao) => {
  const m = parseFloat(media);
  const preenchimento = m - (posicao - 1);
  if (preenchimento >= 1) return 100;
  if (preenchimento <= 0) return 0;
  return Math.round(preenchimento * 100);
};

const novoProduto = ref({
  nome: "",
  descricao: "",
  preco: "",
  quantidadeEstoque: "",
  categoriasIds: [],
});
const arquivoImagemProduto = ref(null);

const abrirModalProduto = () => {
  modalProdutoAberta.value = true;
  erroModalProduto.value = "";
};

const fecharModalProduto = () => {
  modalProdutoAberta.value = false;
  // Reseta os campos ao fechar
  novoProduto.value = {
    nome: "",
    descricao: "",
    preco: "",
    quantidadeEstoque: "",
    categoriasIds: [],
  };
  arquivoImagemProduto.value = null;
};

const capturarImagemProduto = (event) => {
  arquivoImagemProduto.value = event.target.files[0];
};

const salvarProduto = async () => {
  if (novoProduto.value.categoriasIds.length === 0) {
    erroModalProduto.value = "Selecione pelo menos uma categoria.";
    return;
  }

  salvandoProduto.value = true;
  erroModalProduto.value = "";

  try {
    const formData = new FormData();

    const jsonBlob = new Blob([JSON.stringify(novoProduto.value)], {
      type: "application/json",
    });
    formData.append("dados", jsonBlob);

    if (arquivoImagemProduto.value) {
      formData.append("imagem", arquivoImagemProduto.value);
    }

    await api.post("/produtos", formData);

    alert("Produto cadastrado com sucesso!");
    fecharModalProduto();
  } catch (error) {
    console.error("Erro ao salvar produto:", error);
    erroModalProduto.value =
      "Ocorreu um erro ao salvar o produto. Verifique os dados.";
  } finally {
    salvandoProduto.value = false;
  }
};
</script>

<style scoped>
/* O seu CSS intacto, apenas com a adição da classe admin-panel */

.nav-icons a,
.nav-icons .router-link-active,
.nav-icons .router-link-exact-active {
  cursor: pointer;
  display: inline-block;
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
}

.nav-icons a:hover,
.nav-icons .router-link-active:hover {
  color: var(--primary-color) !important;
  transform: translateY(-4px);
}

.navbar {
  padding: 1.5rem 0;
  background: transparent;
}

.cart-count {
  position: absolute;
  top: -5px;
  right: -8px;
  background: var(--primary-color);
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.hero {
  min-height: 80vh;
  display: flex;
  align-items: center;
  padding: 4rem 0;
}

.hero-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-text h1 {
  font-size: clamp(2.5rem, 5vw, 4.5rem);
  font-weight: 600;
  margin-bottom: 1.5rem;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.hero-text p {
  color: var(--text-secondary);
  font-size: 1.15rem;
  margin-bottom: 2rem;
  font-weight: 300;
  max-width: 90%;
}

.hero-img-wrap {
  width: 100%;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
  aspect-ratio: 4/5;
  animation-delay: 0.2s;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Novo estilo inserido para o painel de admin */
.admin-panel {
  background-color: var(--surface-color);
}

.cat-card {
  transition: var(--transition);
  cursor: pointer;
}

.cat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06) !important;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp 1s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

/* Responsividade Básica */
@media (max-width: 992px) {
  .hero-grid {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 3rem;
  }
  .hero-text p {
    margin: 0 auto 2rem;
  }
}

/* CSS do Modal */
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
  z-index: 1000;
}

.modal-content {
  background: var(--bg-color);
  width: 100%;
  max-width: 450px;
  border-radius: var(--radius-lg);
  padding: 2.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);
}

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

/* Reutilizando o input premium do login */
.form-control-premium {
  width: 100%;
  padding: 1rem 1.2rem;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-md);
  font-family: inherit;
  font-size: 0.95rem;
  transition: var(--transition);
  background: #f8fafc;
}

.form-control-premium:focus {
  outline: none;
  border-color: var(--primary-color);
  background: white;
  box-shadow: 0 0 0 4px rgba(15, 23, 42, 0.05);
}
</style>
