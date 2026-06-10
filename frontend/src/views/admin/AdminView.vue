<template>
  <div class="admin-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <div class="container py-5 text-start">


      <div class="row g-4 text-start">


        <!-- Abas de Gerenciamento -->
        <div class="col-12 mt-4 px-0">
          <div class="perfil-card shadow-sm p-4 bg-white rounded-4 border-0">
            <ul class="nav nav-tabs border-bottom-0 mb-4 gap-2 flex-wrap" id="adminTabs" role="tablist">
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'produtos' }"
                  @click="abaAtiva = 'produtos'"
                  type="button"
                >
                  Produtos
                  <span class="badge rounded-pill bg-dark ms-2">{{ produtos.length }}</span>
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'categorias' }"
                  @click="abaAtiva = 'categorias'"
                  type="button"
                >
                  Categorias
                  <span class="badge rounded-pill bg-dark ms-2">{{ categorias.length }}</span>
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'avaliacoes' }"
                  @click="abaAtiva = 'avaliacoes'"
                  type="button"
                >
                  Avaliações
                  <span class="badge rounded-pill bg-dark ms-2">{{ avaliacoes.length }}</span>
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'usuarios' }"
                  @click="abaAtiva = 'usuarios'"
                  type="button"
                >
                  Usuários
                  <span class="badge rounded-pill bg-dark ms-2">{{ usuarios.length }}</span>
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'contato' }"
                  @click="abaAtiva = 'contato'"
                  type="button"
                >
                  Informações de Contato
                </button>
              </li>
            </ul>

            <!-- Conteúdo das Abas -->
            <div class="tab-content">
              <!-- Aba de Produtos -->
              <div v-if="abaAtiva === 'produtos'">
                <!-- Barra de Pesquisa de Produtos -->
                <!-- Barra de Pesquisa de Produtos e Botão Novo -->
                <div class="d-flex mb-4 align-items-center gap-3 flex-wrap">
                  <div class="position-relative flex-grow-1">
                    <span class="position-absolute top-50 translate-middle-y start-0 ps-3 text-muted">
                      <i class="ph ph-magnifying-glass fs-5"></i>
                    </span>
                    <input
                      type="text"
                      v-model="buscaProdutos"
                      class="form-control-premium w-100 ps-5"
                      placeholder="Pesquisar produtos por nome ou categoria..."
                      style="padding-left: 2.75rem !important;"
                    />
                  </div>
                  <button @click="abrirNovoProduto" class="btn-admin-action d-inline-flex align-items-center gap-2">
                    <i class="ph ph-t-shirt fs-5"></i> Novo Produto
                  </button>
                </div>

                <div v-if="produtos.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-package fs-1 mb-2"></i>
                  <p>Nenhum produto cadastrado.</p>
                </div>
                <div v-else-if="produtosFiltradosEOrdenados.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-magnifying-glass fs-1 mb-2"></i>
                  <p>Nenhum produto encontrado para a sua pesquisa.</p>
                </div>
                <div v-else class="table-responsive table-scroll-container">
                  <table class="table align-middle custom-table">
                    <thead>
                      <tr>
                        <th>Produto</th>
                        <th>Preço</th>
                        <th>Desconto</th>
                        <th>Estoque</th>
                        <th>Destaque</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="prod in produtosFiltradosEOrdenados" :key="prod.id">
                        <td>
                          <div class="d-flex align-items-center gap-3">
                            <div class="prod-thumb rounded bg-light overflow-hidden">
                              <img :src="`http://localhost:8080/api/produtos/${prod.id}/imagem`" @error="handleImageError" alt="" class="w-100 h-100 object-fit-cover" />
                            </div>
                            <div>
                              <span class="d-block fw-bold">{{ prod.nome }}</span>
                              <span class="text-muted small">{{ prod.categoriasNomes?.join(', ') || 'Sem categoria' }}</span>
                            </div>
                          </div>
                        </td>
                        <td>
                          <span v-if="prod.desconto > 0" class="text-danger fw-bold">
                            R$ {{ calcularPrecoPromocional(prod.preco, prod.desconto) }}
                            <small class="text-muted text-decoration-line-through d-block fw-normal font-monospace">R$ {{ Number(prod.preco).toFixed(2).replace('.', ',') }}</small>
                          </span>
                          <span v-else class="fw-bold font-monospace">
                            R$ {{ Number(prod.preco).toFixed(2).replace('.', ',') }}
                          </span>
                        </td>
                        <td>
                          <span v-if="prod.desconto > 0" class="badge bg-danger">-{{ prod.desconto }}%</span>
                          <span class="text-muted small" v-else>-</span>
                        </td>
                        <td>{{ prod.quantidadeEstoque }} un.</td>
                        <td>
                          <span v-if="prod.destaqueCarrossel" class="badge bg-success">Sim</span>
                          <span v-else class="badge bg-secondary">Não</span>
                        </td>
                        <td>
                          <div class="d-flex gap-2">
                            <button @click="abrirEditarProduto(prod)" class="btn btn-outline-dark btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-pencil"></i> Editar
                            </button>
                            <button @click="excluirProduto(prod.id)" class="btn btn-outline-danger btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-trash"></i> Excluir
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Aba de Categorias -->
              <div v-if="abaAtiva === 'categorias'">
                <!-- Barra de Pesquisa de Categorias -->
                <!-- Barra de Pesquisa de Categorias e Botão Nova -->
                <div class="d-flex mb-4 align-items-center gap-3 flex-wrap">
                  <div class="position-relative flex-grow-1">
                    <span class="position-absolute top-50 translate-middle-y start-0 ps-3 text-muted">
                      <i class="ph ph-magnifying-glass fs-5"></i>
                    </span>
                    <input
                      type="text"
                      v-model="buscaCategorias"
                      class="form-control-premium w-100 ps-5"
                      placeholder="Pesquisar categorias por nome..."
                      style="padding-left: 2.75rem !important;"
                    />
                  </div>
                  <button @click="abrirNovaCategoria" class="btn-admin-action d-inline-flex align-items-center gap-2">
                    <i class="ph ph-tag fs-5"></i> Nova Categoria
                  </button>
                </div>

                <div v-if="categorias.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-tag fs-1 mb-2"></i>
                  <p>Nenhuma categoria cadastrada.</p>
                </div>
                <div v-else-if="categoriasFiltradasEOrdenadas.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-magnifying-glass fs-1 mb-2"></i>
                  <p>Nenhuma categoria encontrada para a sua pesquisa.</p>
                </div>
                <div v-else class="table-responsive table-scroll-container">
                  <table class="table align-middle custom-table">
                    <thead>
                      <tr>
                        <th>Categoria</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="cat in categoriasFiltradasEOrdenadas" :key="cat.id">
                        <td>
                          <div class="d-flex align-items-center gap-3">
                            <div class="prod-thumb rounded bg-light overflow-hidden">
                              <img :src="`http://localhost:8080/api/categorias/${cat.id}/imagem`" @error="handleImageError" alt="" class="w-100 h-100 object-fit-cover" />
                            </div>
                            <span class="fw-bold">{{ cat.nome }}</span>
                          </div>
                        </td>
                        <td>
                          <div class="d-flex gap-2">
                            <button @click="abrirEditarCategoria(cat)" class="btn btn-outline-dark btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-pencil"></i> Editar
                            </button>
                            <button @click="excluirCategoria(cat.id)" class="btn btn-outline-danger btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-trash"></i> Excluir
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Aba de Avaliações -->
              <div v-if="abaAtiva === 'avaliacoes'">
                <!-- Barra de Pesquisa de Avaliações e Botão Nova -->
                <div class="d-flex mb-4 align-items-center gap-3 flex-wrap">
                  <div class="position-relative flex-grow-1">
                    <span class="position-absolute top-50 translate-middle-y start-0 ps-3 text-muted">
                      <i class="ph ph-magnifying-glass fs-5"></i>
                    </span>
                    <input
                      type="text"
                      v-model="buscaAvaliacoes"
                      class="form-control-premium w-100 ps-5"
                      placeholder="Pesquisar por produto, usuário ou comentário..."
                      style="padding-left: 2.75rem !important;"
                    />
                  </div>
                  <button @click="abrirNovaNovaAvaliacao" class="btn-admin-action d-inline-flex align-items-center gap-2">
                    <i class="ph ph-plus fs-5"></i> Nova Avaliação
                  </button>
                </div>

                <div v-if="avaliacoes.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-star fs-1 mb-2"></i>
                  <p>Nenhuma avaliação cadastrada.</p>
                </div>
                <div v-else-if="avaliacoesFiltradasEOrdenadas.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-magnifying-glass fs-1 mb-2"></i>
                  <p>Nenhuma avaliação encontrada para a sua pesquisa.</p>
                </div>
                <div v-else class="table-responsive table-scroll-container">
                  <table class="table align-middle custom-table">
                    <thead>
                      <tr>
                        <th>Produto</th>
                        <th>Usuário</th>
                        <th>Nota</th>
                        <th>Comentário</th>
                        <th>Data</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="rev in avaliacoesFiltradasEOrdenadas" :key="rev.id">
                        <td class="fw-bold">{{ rev.produtoNome || `ID: ${rev.produtoId}` }}</td>
                        <td>{{ rev.nomeUsuario }}</td>
                        <td>
                          <div class="d-flex text-warning align-items-center gap-1">
                            <i class="ph ph-star-fill"></i>
                            <span class="fw-bold text-dark font-monospace">{{ Number(rev.nota).toFixed(1) }}</span>
                          </div>
                        </td>
                        <td class="text-truncate" style="max-width: 250px;" :title="rev.comentario">{{ rev.comentario || '-' }}</td>
                        <td class="small text-muted font-monospace">{{ formatarData(rev.dataAvaliacao) }}</td>
                        <td>
                          <div class="d-flex gap-2">
                            <button @click="abrirEditarAvaliacao(rev)" class="btn btn-outline-dark btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-pencil"></i> Editar
                            </button>
                            <button @click="excluirAvaliacao(rev.id)" class="btn btn-outline-danger btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-trash"></i> Excluir
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Aba de Usuários -->
              <div v-if="abaAtiva === 'usuarios'">
                <!-- Barra de Pesquisa de Usuários e Botão Novo -->
                <div class="d-flex mb-4 align-items-center gap-3 flex-wrap">
                  <div class="position-relative flex-grow-1">
                    <span class="position-absolute top-50 translate-middle-y start-0 ps-3 text-muted">
                      <i class="ph ph-magnifying-glass fs-5"></i>
                    </span>
                    <input
                      type="text"
                      v-model="buscaUsuarios"
                      class="form-control-premium w-100 ps-5"
                      placeholder="Pesquisar por nome ou e-mail..."
                      style="padding-left: 2.75rem !important;"
                    />
                  </div>
                  <button @click="abrirNovoUsuario" class="btn-admin-action d-inline-flex align-items-center gap-2">
                    <i class="ph ph-user-plus fs-5"></i> Novo Usuário
                  </button>
                </div>

                <div v-if="usuarios.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-users fs-1 mb-2"></i>
                  <p>Nenhum usuário cadastrado.</p>
                </div>
                <div v-else-if="usuariosFiltradosEOrdenados.length === 0" class="text-center py-5 text-muted">
                  <i class="ph ph-magnifying-glass fs-1 mb-2"></i>
                  <p>Nenhum usuário encontrado para a sua pesquisa.</p>
                </div>
                <div v-else class="table-responsive table-scroll-container">
                  <table class="table align-middle custom-table">
                    <thead>
                      <tr>
                        <th>Nome</th>
                        <th>E-mail</th>
                        <th>Papel (Role)</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="user in usuariosFiltradosEOrdenados" :key="user.id">
                        <td class="fw-bold">{{ user.nome }}</td>
                        <td class="font-monospace">{{ user.email }}</td>
                        <td>
                          <span :class="['badge', user.role === 'ADMIN' ? 'bg-dark' : 'bg-secondary']">
                            {{ user.role === 'ADMIN' ? 'Administrador' : 'Cliente' }}
                          </span>
                        </td>
                        <td>
                          <div class="d-flex gap-2">
                            <button @click="abrirEditarUsuario(user)" class="btn btn-outline-dark btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1">
                              <i class="ph ph-pencil"></i> Editar
                            </button>
                            <button @click="excluirUsuario(user.id)" class="btn btn-outline-danger btn-sm rounded-pill px-3 d-inline-flex align-items-center gap-1" :disabled="user.id === usuarioId">
                              <i class="ph ph-trash"></i> Excluir
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Aba de Contato -->
              <div v-if="abaAtiva === 'contato'">
                <h4 class="fw-bold mb-4">Editar Informações de Contato</h4>
                <form @submit.prevent="salvarContatoInfo">
                  <div class="row g-3">
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">TELEFONE</label>
                      <input type="text" v-model="contatoInfo.telefone" class="form-control-premium" required />
                    </div>
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">E-MAIL</label>
                      <input type="email" v-model="contatoInfo.email" class="form-control-premium" required />
                    </div>
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">ENDEREÇO</label>
                      <input type="text" v-model="contatoInfo.endereco" class="form-control-premium" required />
                    </div>
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">HORÁRIO DE ATENDIMENTO</label>
                      <input type="text" v-model="contatoInfo.horario" class="form-control-premium" required />
                    </div>
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">LINK DO FACEBOOK</label>
                      <input type="url" v-model="contatoInfo.linkFacebook" class="form-control-premium" required />
                    </div>
                    <div class="col-md-6">
                      <label class="form-label text-muted small fw-bold">LINK DO INSTAGRAM</label>
                      <input type="url" v-model="contatoInfo.linkInstagram" class="form-control-premium" required />
                    </div>
                  </div>

                  <div v-if="feedbackContato" :class="['alert', isFeedbackContatoErro ? 'alert-danger' : 'alert-success', 'py-2', 'mt-4', 'rounded-3']">
                    {{ feedbackContato }}
                  </div>

                  <div class="mt-4">
                    <button type="submit" class="btn-admin-action" :disabled="salvandoContato">
                      {{ salvandoContato ? "Salvando..." : "Salvar Informações" }}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modais Administrativos -->
    <CategoriaModal
      v-model:open="modalCategoriaAberta"
      :categoriaParaEditar="categoriaParaEditar"
      @categoria-salva="onCategoriaSalva"
    />

    <ProdutoModal
      v-model:open="modalProdutoAberta"
      :categorias="categorias"
      :produtoParaEditar="produtoParaEditar"
      @produto-salvo="onProdutoSalvo"
    />

    <UsuarioModal
      v-model:open="modalUsuarioAberta"
      :usuarioParaEditar="usuarioParaEditar"
      @usuario-salvo="onUsuarioSalvo"
    />

    <AvaliacaoModal
      v-model:open="modalAvaliacaoAberta"
      :produtos="produtos"
      :usuarios="usuarios"
      :avaliacaoParaEditar="avaliacaoParaEditar"
      @avaliacao-salvo="onAvaliacaoSalvo"
    />

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

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import Navbar from "../../components/layout/Navbar.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import Footer from "../../components/layout/Footer.vue";

import CategoriaModal from "../../components/modal/CategoriaModal.vue";
import ProdutoModal from "../../components/modal/ProductModal.vue";
import UsuarioModal from "../../components/modal/UsuarioModal.vue";
import AvaliacaoModal from "../../components/modal/AvaliacaoModal.vue";
import api from "../../services/api.js";
import { formatarData } from "../../utils/date.js";

const router = useRouter();

const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");
const usuarioId = ref(null);

const categorias = ref([]);
const produtos = ref([]);
const usuarios = ref([]);
const avaliacoes = ref([]);
const abaAtiva = ref("produtos");

const buscaProdutos = ref("");
const buscaCategorias = ref("");
const buscaAvaliacoes = ref("");
const buscaUsuarios = ref("");

const modalCategoriaAberta = ref(false);
const modalProdutoAberta = ref(false);
const modalUsuarioAberta = ref(false);
const modalAvaliacaoAberta = ref(false);

const categoriaParaEditar = ref(null);
const produtoParaEditar = ref(null);
const usuarioParaEditar = ref(null);
const avaliacaoParaEditar = ref(null);

const itensCarrinho = ref([]);
const subtotalCarrinho = ref(0);
const sidebarAberta = ref(false);

// Contato Info
const contatoInfo = ref({
  telefone: "",
  email: "",
  endereco: "",
  horario: "",
  linkFacebook: "",
  linkInstagram: ""
});
const salvandoContato = ref(false);
const feedbackContato = ref("");
const isFeedbackContatoErro = ref(false);

const quantidadeCarrinho = computed(() => {
  return itensCarrinho.value.length;
});

const produtosFiltradosEOrdenados = computed(() => {
  let resultado = [...produtos.value];
  if (buscaProdutos.value.trim()) {
    const termo = buscaProdutos.value.toLowerCase().trim();
    resultado = resultado.filter(prod => 
      prod.nome.toLowerCase().includes(termo) || 
      (prod.categoriasNomes && prod.categoriasNomes.some(cat => cat.toLowerCase().includes(termo)))
    );
  }
  resultado.sort((a, b) => a.nome.localeCompare(b.nome, "pt-BR"));
  return resultado;
});

const categoriasFiltradasEOrdenadas = computed(() => {
  let resultado = [...categorias.value];
  if (buscaCategorias.value.trim()) {
    const termo = buscaCategorias.value.toLowerCase().trim();
    resultado = resultado.filter(cat => 
      cat.nome.toLowerCase().includes(termo)
    );
  }
  resultado.sort((a, b) => a.nome.localeCompare(b.nome, "pt-BR"));
  return resultado;
});

const usuariosFiltradosEOrdenados = computed(() => {
  let resultado = [...usuarios.value];
  if (buscaUsuarios.value.trim()) {
    const termo = buscaUsuarios.value.toLowerCase().trim();
    resultado = resultado.filter(user => 
      user.nome.toLowerCase().includes(termo) || 
      user.email.toLowerCase().includes(termo)
    );
  }
  resultado.sort((a, b) => a.nome.localeCompare(b.nome, "pt-BR"));
  return resultado;
});

const avaliacoesFiltradasEOrdenadas = computed(() => {
  let resultado = [...avaliacoes.value];
  if (buscaAvaliacoes.value.trim()) {
    const termo = buscaAvaliacoes.value.toLowerCase().trim();
    resultado = resultado.filter(rev => {
      const prodNome = rev.produtoNome ? rev.produtoNome.toLowerCase() : `id: ${rev.produtoId}`;
      const userNome = rev.nomeUsuario ? rev.nomeUsuario.toLowerCase() : "";
      const comentario = rev.comentario ? rev.comentario.toLowerCase() : "";
      return prodNome.includes(termo) || userNome.includes(termo) || comentario.includes(termo);
    });
  }
  resultado.sort((a, b) => {
    const prodA = a.produtoNome || `ID: ${a.produtoId}`;
    const prodB = b.produtoNome || `ID: ${b.produtoId}`;
    const compProd = prodA.localeCompare(prodB, "pt-BR");
    if (compProd !== 0) return compProd;
    const userA = a.nomeUsuario || "";
    const userB = b.nomeUsuario || "";
    return userA.localeCompare(userB, "pt-BR");
  });
  return resultado;
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

const carregarIdentidade = () => {
  const token = localStorage.getItem("token_vestebem");
  if (!token) {
    router.push("/");
    return false;
  }

  estaLogado.value = true;
  const dadosToken = decodificarJWT(token);

  if (!dadosToken) {
    router.push("/");
    return false;
  }

  usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

  const permissao = dadosToken.role || "";
  isAdmin.value = permissao.toUpperCase() === "ADMIN";

  if (!isAdmin.value) {
    router.push("/");
    return false;
  }

  if (dadosToken.sub) {
    nomeUsuario.value = dadosToken.sub.split("@")[0];
  }
  return true;
};

const carregarUsuario = async () => {
  if (usuarioId.value) {
    try {
      const resposta = await api.get(`/usuarios/${usuarioId.value}`);
      const nomeCompleto = resposta.data.nome || "";
      nomeUsuario.value = nomeCompleto.split(" ")[0]; // apenas o primeiro nome
    } catch {
      // Fallback
    }
  }
};

const carregarCategorias = async () => {
  try {
    const resposta = await api.get("/categorias");
    categorias.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar categorias:", error);
  }
};

const carregarProdutos = async () => {
  try {
    const resposta = await api.get("/produtos");
    produtos.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar produtos:", error);
  }
};

const carregarUsuarios = async () => {
  try {
    const resposta = await api.get("/usuarios");
    usuarios.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar usuários:", error);
  }
};

const carregarAvaliacoes = async () => {
  try {
    const resposta = await api.get("/avaliacoes");
    avaliacoes.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar avaliações:", error);
  }
};

const carregarContatoInfo = async () => {
  try {
    const resposta = await api.get("/contato");
    if (resposta.data) {
      contatoInfo.value = resposta.data;
    }
  } catch (error) {
    console.error("Erro ao carregar contato no admin:", error);
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

// Ações de Abertura de Modais
const abrirNovaCategoria = () => {
  categoriaParaEditar.value = null;
  modalCategoriaAberta.value = true;
};

const abrirEditarCategoria = (cat) => {
  categoriaParaEditar.value = cat;
  modalCategoriaAberta.value = true;
};

const abrirNovoProduto = () => {
  produtoParaEditar.value = null;
  modalProdutoAberta.value = true;
};

const abrirEditarProduto = (prod) => {
  produtoParaEditar.value = prod;
  modalProdutoAberta.value = true;
};

const abrirNovoUsuario = () => {
  usuarioParaEditar.value = null;
  modalUsuarioAberta.value = true;
};

const abrirEditarUsuario = (user) => {
  usuarioParaEditar.value = user;
  modalUsuarioAberta.value = true;
};

const abrirNovaNovaAvaliacao = () => {
  avaliacaoParaEditar.value = null;
  modalAvaliacaoAberta.value = true;
};

const abrirEditarAvaliacao = (rev) => {
  avaliacaoParaEditar.value = rev;
  modalAvaliacaoAberta.value = true;
};

// Ações de Exclusão
const excluirProduto = async (id) => {
  if (confirm("Tem certeza que deseja excluir este produto?")) {
    try {
      await api.delete(`/produtos/${id}`);
      await carregarProdutos();
    } catch (error) {
      alert(error.response?.data || "Erro ao excluir produto.");
    }
  }
};

const excluirCategoria = async (id) => {
  if (confirm("Tem certeza que deseja excluir esta categoria?")) {
    try {
      await api.delete(`/categorias/${id}`);
      await carregarCategorias();
    } catch (error) {
      alert(error.response?.data || "Erro ao excluir categoria. Verifique se existem produtos associados.");
    }
  }
};

const excluirUsuario = async (id) => {
  if (confirm("Tem certeza que deseja excluir este usuário?")) {
    try {
      await api.delete(`/usuarios/${id}`);
      await carregarUsuarios();
    } catch (error) {
      alert(error.response?.data || "Erro ao excluir usuário.");
    }
  }
};

const excluirAvaliacao = async (id) => {
  if (confirm("Tem certeza que deseja excluir esta avaliação?")) {
    try {
      await api.delete(`/avaliacoes/${id}`);
      await carregarAvaliacoes();
    } catch (error) {
      alert(error.response?.data || "Erro ao excluir avaliação.");
    }
  }
};

// Salvar Informações de Contato
const salvarContatoInfo = async () => {
  salvandoContato.value = true;
  feedbackContato.value = "";
  isFeedbackContatoErro.value = false;

  try {
    await api.put("/contato", contatoInfo.value);
    feedbackContato.value = "Informações de contato salvas com sucesso!";
  } catch (error) {
    console.error(error);
    feedbackContato.value = error.response?.data || "Erro ao salvar informações de contato.";
    isFeedbackContatoErro.value = true;
  } finally {
    salvandoContato.value = false;
  }
};

// Callbacks de Salvamento
const onCategoriaSalva = async () => {
  await carregarCategorias();
};

const onProdutoSalvo = async () => {
  await Promise.all([carregarCategorias(), carregarProdutos()]);
};

const onUsuarioSalvo = async () => {
  await carregarUsuarios();
};

const onAvaliacaoSalvo = async () => {
  await carregarAvaliacoes();
};

const fazerLogout = () => {
  localStorage.removeItem("token_vestebem");
  estaLogado.value = false;
  isAdmin.value = false;
  nomeUsuario.value = "";
  usuarioId.value = null;
  itensCarrinho.value = [];
  subtotalCarrinho.value = 0;
  router.push("/");
};

const removerItemSidebar = async (produtoId) => {
  try {
    const resposta = await api.delete(
      `/carrinhos/${usuarioId.value}/remover/${produtoId}`
    );
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch (error) {
    alert(error.response?.data || "Erro ao remover item.");
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
    alert(error.response?.data || "Erro ao adicionar item.");
  }
};

const calcularPrecoPromocional = (preco, desconto) => {
  const pOriginal = Number(preco);
  const pFinal = pOriginal * (1 - desconto / 100);
  return pFinal.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};


const handleImageError = (event) => {
  event.target.style.display = 'none';
};

onMounted(async () => {
  const autenticado = carregarIdentidade();
  if (autenticado) {
    await carregarUsuario();
    await Promise.all([
      carregarCategorias(),
      carregarProdutos(),
      carregarUsuarios(),
      carregarAvaliacoes(),
      carregarContatoInfo(),
      carregarCarrinho()
    ]);
  }
});
</script>

<style scoped>
.admin-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color, #f8fafc);
}

.admin-header-card {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
}

.backdrop-blur {
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.stat-card {
  min-width: 130px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.perfil-card {
  background: white;
  border-radius: var(--radius-lg, 16px);
  border: 1px solid rgba(0, 0, 0, 0.05) !important;
}

.nav-tabs .nav-link {
  color: #64748b;
  font-weight: 500;
  transition: all 0.2s ease;
  background: #f1f5f9;
}

.nav-tabs .nav-link.active {
  color: white;
  background: #111 !important;
  font-weight: 600;
}

.custom-table {
  margin-top: 1rem;
}

.custom-table th {
  border-bottom: 2px solid #f1f5f9;
  color: #475569;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 1rem;
}

.custom-table td {
  padding: 1rem;
  border-bottom: 1px solid #f1f5f9;
  color: #334155;
}

.prod-thumb {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
}

/* Carrinho Flutuante */
.btn-carrinho-flutuante {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color, #111);
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

.table-scroll-container {
  max-height: calc(35.5 * 66px); /* Altura correspondente a aproximadamente 35.5 itens com paddings e bordas */
  overflow-y: auto;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
}

.table-scroll-container thead th {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 2;
  box-shadow: inset 0 -2px 0 #f1f5f9;
}

.btn-admin-action {
  border: none;
  padding: 0.85rem 1.5rem;
  border-radius: var(--radius-md, 8px);
  background-color: var(--primary-color, #111);
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
  text-decoration: none;
}

.btn-admin-action:hover {
  transform: translateY(-2px);
  background-color: #333333;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  color: white;
}
</style>
