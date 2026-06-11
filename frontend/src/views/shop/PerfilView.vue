<template>
  <div class="perfil-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <div class="container py-5">
      <div v-if="carregandoUsuario" class="text-center py-5">
        <div class="spinner-border text-dark" role="status"></div>
        <p class="mt-3 text-muted">A carregar os seus dados...</p>
      </div>

      <div v-else class="row g-5 text-start">
        <!-- Coluna da Esquerda: Dados Pessoais -->
        <div class="col-lg-4">
          <div class="perfil-card shadow-sm p-4 bg-white rounded-4 border-0">
            <div class="text-center mb-4">
              <div class="avatar-placeholder mx-auto mb-3">
                <i class="ph ph-user fs-1"></i>
              </div>
              <h4 class="fw-bold mb-1">{{ dadosUsuario.nome }}</h4>
              <span class="badge rounded-pill bg-dark px-3 py-2 fw-normal">
                {{ isAdmin ? 'Administrador' : 'Cliente' }}
              </span>
            </div>

            <hr class="my-4 text-muted" />

            <h5 class="fw-bold mb-4 d-flex align-items-center gap-2">
              <i class="ph ph-pencil-simple-line"></i>
              Editar Dados
            </h5>

            <form @submit.prevent="salvarDados">
              <div class="mb-3">
                <label for="nome" class="form-label text-muted small fw-bold">Nome Completo</label>
                <input
                  type="text"
                  id="nome"
                  v-model="formDados.nome"
                  class="form-control form-premium"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="email" class="form-label text-muted small fw-bold">E-mail</label>
                <input
                  type="email"
                  id="email"
                  v-model="formDados.email"
                  class="form-control form-premium"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="senha" class="form-label text-muted small fw-bold">Nova Senha (Opcional)</label>
                <input
                  type="password"
                  id="senha"
                  v-model="formDados.senha"
                  placeholder="Mínimo de 6 caracteres"
                  class="form-control form-premium"
                />
              </div>

              <div class="mb-4">
                <label for="confirmarSenha" class="form-label text-muted small fw-bold">Confirmar Senha</label>
                <input
                  type="password"
                  id="confirmarSenha"
                  v-model="formDados.confirmarSenha"
                  placeholder="Repita a nova senha"
                  class="form-control form-premium"
                />
              </div>

              <!-- Alertas de Feedback -->
              <div v-if="feedbackMsg" :class="['alert', isFeedbackErro ? 'alert-danger' : 'alert-success', 'py-2', 'small', 'rounded-3']">
                {{ feedbackMsg }}
              </div>

              <button
                type="submit"
                class="btn btn-dark w-100 py-3 rounded-pill fw-bold btn-salvar-perfil"
                :disabled="salvando"
              >
                <span v-if="salvando" class="spinner-border spinner-border-sm me-2" role="status"></span>
                Salvar Alterações
              </button>
            </form>
          </div>
        </div>

        <!-- Coluna da Direita: Histórico de Pedidos -->
        <div class="col-lg-8">
          <div class="perfil-card shadow-sm p-4 bg-white rounded-4 border-0 h-100">
            <h4 class="fw-bold mb-4 d-flex align-items-center gap-2">
              <i class="ph ph-receipt"></i>
              Meus Pedidos
            </h4>

            <!-- Abas (Tabs) -->
            <ul class="nav nav-tabs border-bottom-0 mb-4 gap-2" id="orderTabs" role="tablist">
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'abertos' }"
                  @click="abaAtiva = 'abertos'"
                  type="button"
                >
                  Abertos
                  <span v-if="pedidosAbertos.length > 0" class="badge rounded-pill bg-dark ms-2">
                    {{ pedidosAbertos.length }}
                  </span>
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link rounded-pill px-4 py-2 border-0"
                  :class="{ active: abaAtiva === 'completos' }"
                  @click="abaAtiva = 'completos'"
                  type="button"
                >
                  Concluídos
                  <span v-if="pedidosCompletos.length > 0" class="badge rounded-pill bg-dark ms-2">
                    {{ pedidosCompletos.length }}
                  </span>
                </button>
              </li>
            </ul>

            <!-- Conteúdo das Abas -->
            <div class="tab-content">
              <div v-if="carregandoPedidos" class="text-center py-5">
                <div class="spinner-border text-dark spinner-border-sm" role="status"></div>
                <p class="mt-2 text-muted small">Carregando seus pedidos...</p>
              </div>

              <div v-else>
                <!-- Aba Pedidos Abertos -->
                <div v-if="abaAtiva === 'abertos'">
                  <div v-if="pedidosAbertos.length === 0" class="text-center py-5 text-muted">
                    <i class="ph ph-package fs-1 mb-2"></i>
                    <p>Nenhum pedido aberto no momento.</p>
                  </div>
                  <div v-else class="orders-list d-flex flex-column gap-3">
                    <div
                      v-for="pedido in pedidosAbertos"
                      :key="pedido.id"
                      class="order-card p-4 rounded-4 border bg-light"
                    >
                      <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                        <div>
                          <span class="text-muted small fw-bold">PEDIDO #{{ pedido.id }}</span>
                          <div class="small text-muted">{{ formatarData(pedido.dataCriacao, true) }}</div>
                        </div>
                        <span :class="['badge-status', getStatusClass(pedido.status)]">
                          {{ getStatusLabel(pedido.status) }}
                        </span>
                      </div>

                      <div v-if="pedido.metodoPagamento" class="mb-3 d-flex align-items-center gap-2 small text-muted">
                        <i class="ph ph-wallet"></i>
                        <span>Pagamento: <strong>{{ pedido.metodoPagamento }}</strong></span>
                      </div>

                      <div class="order-items-box mb-3">
                        <div v-for="item in pedido.itens" :key="item.id" class="d-flex justify-content-between py-2 border-bottom border-light small text-secondary">
                          <span>{{ item.produtoNome }} <strong class="text-dark">x{{ item.quantidade }}</strong></span>
                          <span>R$ {{ Number(item.precoUnitario).toFixed(2).replace('.', ',') }}</span>
                        </div>
                      </div>

                      <div class="d-flex justify-content-between align-items-center pt-2">
                        <span class="fw-bold">Total</span>
                        <span class="fw-bold fs-5 text-primary">R$ {{ Number(pedido.totalPedido).toFixed(2).replace('.', ',') }}</span>
                      </div>

                      <div v-if="pedido.status === 'PENDENTE'" class="d-flex justify-content-end mt-3 pt-2 border-top border-light">
                        <button
                          class="btn btn-dark btn-sm rounded-pill px-4"
                          @click="irParaPagamento(pedido.id)"
                        >
                          <i class="ph ph-credit-card me-1"></i> Concluir Pagamento
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Aba Pedidos Completos -->
                <div v-if="abaAtiva === 'completos'">
                  <div v-if="pedidosCompletos.length === 0" class="text-center py-5 text-muted">
                    <i class="ph ph-folder-open fs-1 mb-2"></i>
                    <p>Nenhum pedido concluído ou cancelado encontrado.</p>
                  </div>
                  <div v-else class="orders-list d-flex flex-column gap-3">
                    <div
                      v-for="pedido in pedidosCompletos"
                      :key="pedido.id"
                      class="order-card p-4 rounded-4 border bg-light"
                    >
                      <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                        <div>
                          <span class="text-muted small fw-bold">PEDIDO #{{ pedido.id }}</span>
                          <div class="small text-muted">{{ formatarData(pedido.dataCriacao, true) }}</div>
                        </div>
                        <span :class="['badge-status', getStatusClass(pedido.status)]">
                          {{ getStatusLabel(pedido.status) }}
                        </span>
                      </div>

                      <div v-if="pedido.metodoPagamento" class="mb-3 d-flex align-items-center gap-2 small text-muted">
                        <i class="ph ph-wallet"></i>
                        <span>Pagamento: <strong>{{ pedido.metodoPagamento }}</strong></span>
                      </div>

                      <div class="order-items-box mb-3">
                        <div v-for="item in pedido.itens" :key="item.id" class="d-flex justify-content-between align-items-center py-2 border-bottom border-light small text-secondary">
                          <span>{{ item.produtoNome }} <strong class="text-dark">x{{ item.quantidade }}</strong></span>
                          <div class="d-flex align-items-center gap-3">
                            <span>R$ {{ Number(item.precoUnitario).toFixed(2).replace('.', ',') }}</span>
                            <!-- Botão / Estado da Avaliação -->
                            <button
                              v-if="(pedido.status.toUpperCase() === 'CONCLUIDO' || pedido.status.toUpperCase() === 'ENTREGUE') && !produtosAvaliadosIds.includes(item.produtoId)"
                              class="btn btn-outline-dark btn-xs rounded-pill px-3 py-1"
                              style="font-size: 0.75rem;"
                              @click="abrirModalAvaliacao(item.produtoId, item.produtoNome)"
                            >
                              <i class="ph ph-star me-1"></i> Avaliar
                            </button>
                            <span
                              v-else-if="(pedido.status.toUpperCase() === 'CONCLUIDO' || pedido.status.toUpperCase() === 'ENTREGUE') && produtosAvaliadosIds.includes(item.produtoId)"
                              class="text-success small fw-semibold d-inline-flex align-items-center gap-1"
                            >
                              <i class="ph ph-check-circle"></i> Avaliado
                            </span>
                          </div>
                        </div>
                      </div>

                      <div class="d-flex justify-content-between align-items-center pt-2">
                        <span class="fw-bold">Total</span>
                        <span class="fw-bold fs-5 text-secondary">R$ {{ Number(pedido.totalPedido).toFixed(2).replace('.', ',') }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
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

    <!-- Modal de Avaliação Interativo -->
    <div v-if="modalAvaliacaoAberto" class="modal-overlay" @click="fecharModalAvaliacao">
      <div class="modal-content-custom fade-in-up" @click.stop>
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h4 class="fw-bold mb-0">Avaliar Produto</h4>
          <button @click="fecharModalAvaliacao" class="btn-close-modal">
            <i class="ph ph-x"></i>
          </button>
        </div>

        <div class="mb-3 text-start">
          <p class="text-muted small mb-1">PRODUTO</p>
          <p class="fw-bold text-dark mb-0">{{ produtoParaAvaliarNome }}</p>
        </div>

        <form @submit.prevent="enviarAvaliacao">
          <!-- Estrelas de Avaliação (Dropdown Customizado) -->
          <div class="mb-4 text-start position-relative">
            <label class="form-label text-muted small fw-bold d-block mb-2">SUA NOTA</label>
            
            <div class="custom-select-wrapper">
              <!-- Gatilho do select customizado -->
              <div 
                class="custom-select-trigger" 
                :class="{ 'custom-select-trigger--open': selectNotaAberto }"
                @click="toggleSelectNota"
              >
                <i class="ph ph-star select-trigger-icon text-warning"></i>
                <span class="select-trigger-text text-dark fw-semibold">
                  {{ obterLabelNotaSelecionada() }}
                </span>
                <i class="ph ph-caret-down select-trigger-caret"></i>
              </div>

              <!-- Menu suspenso de opções customizadas -->
              <transition name="slide-up">
                <div class="custom-options-menu" v-if="selectNotaAberto">
                  <div 
                    v-for="op in opcoesNotas" 
                    :key="op.valor" 
                    class="custom-option-item"
                    :class="{ 'custom-option-item--selected': formAvaliacao.nota === op.valor }"
                    @click="selecionarNota(op.valor)"
                  >
                    <span class="option-qty fw-bold text-dark">{{ op.label }}</span>
                    <i v-if="formAvaliacao.nota === op.valor" class="ph ph-check text-success"></i>
                  </div>
                </div>
              </transition>
            </div>
          </div>

          <!-- Comentário -->
          <div class="mb-4 text-start">
            <label for="comentario" class="form-label text-muted small fw-bold">COMENTÁRIO (OPCIONAL)</label>
            <textarea
              id="comentario"
              v-model="formAvaliacao.comentario"
              rows="4"
              class="form-control form-premium text-start"
              placeholder="O que achou deste produto? Conte sua experiência..."
              maxlength="500"
            ></textarea>
            <div class="d-flex justify-content-end mt-1">
              <span class="text-muted small">{{ formAvaliacao.comentario ? formAvaliacao.comentario.length : 0 }}/500</span>
            </div>
          </div>

          <button
            type="submit"
            class="btn btn-dark w-100 py-3 rounded-pill fw-bold btn-enviar-avaliacao"
            :disabled="enviandoAvaliacao"
          >
            <span v-if="enviandoAvaliacao" class="spinner-border spinner-border-sm me-2" role="status"></span>
            Enviar Avaliação
          </button>
        </form>
      </div>
    </div>

    <!-- Rodapé Premium -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import Navbar from "../../components/layout/Navbar.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import Footer from "../../components/layout/Footer.vue";
import api from "../../services/api.js";
import { showToast } from "../../services/toast";
import { formatarData } from "../../utils/date.js";

const router = useRouter();

const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");
const usuarioId = ref(null);

const carregandoUsuario = ref(true);
const carregandoPedidos = ref(true);
const salvando = ref(false);

const dadosUsuario = ref({ nome: "", email: "" });
const formDados = ref({ nome: "", email: "", senha: "", confirmarSenha: "" });
const orders = ref([]);
const abaAtiva = ref("abertos");

const feedbackMsg = ref("");

// Estados da Avaliação
const produtosAvaliadosIds = ref([]);
const modalAvaliacaoAberto = ref(false);
const produtoParaAvaliarId = ref(null);
const produtoParaAvaliarNome = ref("");
const hoverNota = ref(null);
const enviandoAvaliacao = ref(false);
const formAvaliacao = ref({
  nota: 5.0,
  comentario: "",
});

const selectNotaAberto = ref(false);
const opcoesNotas = [
  { valor: 0.5, label: "0,5 Estrela" },
  { valor: 1.0, label: "1,0 Estrela" },
  { valor: 1.5, label: "1,5 Estrelas" },
  { valor: 2.0, label: "2,0 Estrelas" },
  { valor: 2.5, label: "2,5 Estrelas" },
  { valor: 3.0, label: "3,0 Estrelas" },
  { valor: 3.5, label: "3,5 Estrelas" },
  { valor: 4.0, label: "4,0 Estrelas" },
  { valor: 4.5, label: "4,5 Estrelas" },
  { valor: 5.0, label: "5,0 Estrelas" },
];
const isFeedbackErro = ref(false);

const itensCarrinho = ref([]);
const subtotalCarrinho = ref(0);
const sidebarAberta = ref(false);

const quantidadeCarrinho = computed(() => {
  return itensCarrinho.value.length;
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
    router.push("/login");
    return false;
  }

  estaLogado.value = true;
  const dadosToken = decodificarJWT(token);

  if (!dadosToken) {
    router.push("/login");
    return false;
  }

  usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

  const cachedName = localStorage.getItem("nome_usuario_vestebem");
  if (cachedName) {
    nomeUsuario.value = cachedName;
  } else if (dadosToken.sub) {
    nomeUsuario.value = dadosToken.sub.split("@")[0];
  }

  const permissao = dadosToken.role || "";
  isAdmin.value = permissao.toUpperCase() === "ADMIN";
  return true;
};

const obterDadosUsuario = async () => {
  try {
    carregandoUsuario.value = true;
    const resposta = await api.get(`/usuarios/${usuarioId.value}`);
    dadosUsuario.value = resposta.data;
    formDados.value.nome = resposta.data.nome;
    formDados.value.email = resposta.data.email;
    formDados.value.senha = "";
    formDados.value.confirmarSenha = "";
    
    if (resposta.data.nome) {
      const primeiroNome = resposta.data.nome.split(" ")[0];
      nomeUsuario.value = primeiroNome;
      localStorage.setItem("nome_usuario_vestebem", primeiroNome);
    }
  } catch (err) {
    console.error("Erro ao carregar dados do usuário:", err);
  } finally {
    carregandoUsuario.value = false;
  }
};

const carregarPedidos = async () => {
  try {
    carregandoPedidos.value = true;
    const resposta = await api.get(`/pedidos/usuario/${usuarioId.value}`);
    orders.value = resposta.data || [];
  } catch (err) {
    console.error("Erro ao obter histórico de pedidos:", err);
  } finally {
    carregandoPedidos.value = false;
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

const pedidosAbertos = computed(() => {
  return orders.value.filter((o) => {
    const s = (o.status || "").toUpperCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    return s !== "CONCLUIDO" && s !== "CANCELADO" && s !== "ENTREGUE";
  });
});

const pedidosCompletos = computed(() => {
  return orders.value.filter((o) => {
    const s = (o.status || "").toUpperCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    return s === "CONCLUIDO" || s === "CANCELADO" || s === "ENTREGUE";
  });
});

const salvarDados = async () => {
  feedbackMsg.value = "";
  isFeedbackErro.value = false;

  if (formDados.value.senha) {
    if (formDados.value.senha.length < 6) {
      feedbackMsg.value = "A nova senha deve ter pelo menos 6 caracteres.";
      isFeedbackErro.value = true;
      return;
    }
    if (formDados.value.senha !== formDados.value.confirmarSenha) {
      feedbackMsg.value = "A confirmação de senha não confere.";
      isFeedbackErro.value = true;
      return;
    }
  }

  try {
    salvando.value = true;
    const resposta = await api.put(`/usuarios/${usuarioId.value}`, {
      nome: formDados.value.nome,
      email: formDados.value.email,
      senha: formDados.value.senha || null,
    });

    feedbackMsg.value = "Dados atualizados com sucesso!";
    dadosUsuario.value = resposta.data;
    
    // Atualiza o nome exibido localmente e o cache
    const primeiroNome = (resposta.data.nome || "").split(" ")[0];
    nomeUsuario.value = primeiroNome;
    localStorage.setItem("nome_usuario_vestebem", primeiroNome);

    formDados.value.senha = "";
    formDados.value.confirmarSenha = "";
  } catch (err) {
    feedbackMsg.value = err.response?.data || "Erro ao salvar alterações.";
    isFeedbackErro.value = true;
  } finally {
    salvando.value = false;
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


const carregarAvaliacoesUsuario = async () => {
  if (!usuarioId.value) return;
  try {
    const resposta = await api.get(`/avaliacoes/usuario/${usuarioId.value}`);
    produtosAvaliadosIds.value = (resposta.data || []).map((av) => av.produtoId);
  } catch (err) {
    console.error("Erro ao carregar avaliações do usuário:", err);
  }
};

const toggleSelectNota = () => {
  selectNotaAberto.value = !selectNotaAberto.value;
};

const selecionarNota = (valor) => {
  formAvaliacao.value.nota = valor;
  selectNotaAberto.value = false;
};

const obterLabelNotaSelecionada = () => {
  return opcoesNotas.find((o) => o.valor === formAvaliacao.value.nota)?.label || "5,0 Estrelas";
};

const fecharSelectNotaClickOutside = (e) => {
  if (!e.target.closest('.custom-select-wrapper')) {
    selectNotaAberto.value = false;
  }
};

const abrirModalAvaliacao = (produtoId, produtoNome) => {
  produtoParaAvaliarId.value = produtoId;
  produtoParaAvaliarNome.value = produtoNome;
  formAvaliacao.value.nota = 5.0;
  formAvaliacao.value.comentario = "";
  hoverNota.value = null;
  selectNotaAberto.value = false;
  modalAvaliacaoAberto.value = true;
  document.body.style.overflow = "hidden";
};

const fecharModalAvaliacao = () => {
  modalAvaliacaoAberto.value = false;
  produtoParaAvaliarId.value = null;
  produtoParaAvaliarNome.value = "";
  formAvaliacao.value.nota = 5.0;
  formAvaliacao.value.comentario = "";
  hoverNota.value = null;
  selectNotaAberto.value = false;
  document.body.style.overflow = "";
};

const enviarAvaliacao = async () => {
  if (!usuarioId.value || !produtoParaAvaliarId.value) return;
  try {
    enviandoAvaliacao.value = true;
    await api.post(`/avaliacoes/${usuarioId.value}/produto/${produtoParaAvaliarId.value}`, {
      nota: Number(formAvaliacao.value.nota),
      comentario: formAvaliacao.value.comentario,
    });
    
    showToast("Avaliação enviada com sucesso!", "success");
    produtosAvaliadosIds.value.push(produtoParaAvaliarId.value);
    fecharModalAvaliacao();
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao enviar avaliação.");
    showToast(msg, "error");
  } finally {
    enviandoAvaliacao.value = false;
  }
};

const getStatusClass = (status) => {
  if (!status) return "";
  const s = status.toUpperCase();
  if (s === "PENDENTE") return "status-pendente";
  if (s === "PAGO" || s === "A_CAMINHO" || s === "PROCESSANDO") return "status-processando";
  if (s === "ENTREGUE" || s === "CONCLUIDO") return "status-entregue";
  if (s === "CANCELADO") return "status-cancelado";
  return "status-padrao";
};

const getStatusLabel = (status) => {
  if (!status) return "";
  const labels = {
    PENDENTE: "Pendente",
    PAGO: "Pago",
    PROCESSANDO: "Processando",
    A_CAMINHO: "A caminho",
    ENTREGUE: "Concluído",
    CONCLUIDO: "Concluído",
    CANCELADO: "Cancelado",
  };
  return labels[status.toUpperCase()] || status;
};

const irParaPagamento = (pedidoId) => {
  router.push({ path: "/pagamento", query: { pedidoId } });
};

onMounted(async () => {
  window.addEventListener("click", fecharSelectNotaClickOutside);
  const autenticado = carregarIdentidade();
  if (autenticado) {
    await Promise.all([
      obterDadosUsuario(),
      carregarPedidos(),
      carregarCarrinho(),
      carregarAvaliacoesUsuario(),
    ]);
  }
});

onUnmounted(() => {
  window.removeEventListener("click", fecharSelectNotaClickOutside);
});
</script>

<style scoped>
.perfil-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color, #f8fafc);
}

.perfil-card {
  background: var(--bg-color, white);
  border-radius: var(--radius-lg, 16px);
  border: 1px solid var(--border-color, rgba(0, 0, 0, 0.05)) !important;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  background: var(--bg-color, #f1f5f9);
  color: var(--text-secondary, #475569);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-premium {
  border: 1px solid var(--border-color, #e2e8f0);
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 0.95rem;
  background-color: var(--surface-color, #fafafa);
  color: var(--text-primary, #1d1d1f);
  transition: all 0.2s ease;
}

.form-premium:focus {
  border-color: var(--primary-color, #111);
  background-color: var(--bg-color, #fff);
  box-shadow: 0 0 0 3px rgba(17, 17, 17, 0.05);
}

.btn-salvar-perfil {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.btn-salvar-perfil:hover:not(:disabled) {
  background: #222;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
}

.btn-salvar-perfil:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Pedidos */
.nav-tabs .nav-link {
  color: var(--text-secondary, #64748b);
  font-weight: 500;
  transition: all 0.2s ease;
  background: var(--surface-color, #f1f5f9);
}

.nav-tabs .nav-link.active {
  color: var(--bg-color, #ffffff);
  background: var(--primary-color, #111) !important;
  font-weight: 600;
}

.order-card {
  background: var(--bg-color, #ffffff) !important;
  border: 1px solid var(--border-color, #e2e8f0) !important;
  transition: all 0.3s ease;
}

.order-card:hover {
  border-color: var(--border-color, #cbd5e1) !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
}

.badge-status {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-pendente {
  background: rgba(217, 119, 6, 0.15);
  color: #d97706;
  border: 1px solid rgba(217, 119, 6, 0.3);
}

.status-processando {
  background: rgba(37, 99, 235, 0.15);
  color: #2563eb;
  border: 1px solid rgba(37, 99, 235, 0.3);
}

.status-entregue {
  background: rgba(22, 163, 74, 0.15);
  color: #16a34a;
  border: 1px solid rgba(22, 163, 74, 0.3);
}

.status-cancelado {
  background: rgba(220, 38, 38, 0.15);
  color: #dc2626;
  border: 1px solid rgba(220, 38, 38, 0.3);
}

.status-padrao {
  background: rgba(71, 85, 105, 0.15);
  color: var(--text-secondary, #475569);
  border: 1px solid rgba(71, 85, 105, 0.3);
}

/* Carrinho Flutuante */
.btn-carrinho-flutuante {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color, #111);
  color: var(--bg-color, white);
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

/* Modal de Avaliação Customizado */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  z-index: 10000;
}

.modal-content-custom {
  width: 100%;
  max-width: 480px;
  background: var(--bg-color, white);
  border-radius: 20px;
  padding: 2.5rem 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  position: relative;
}

.btn-close-modal {
  border: none;
  background: transparent;
  font-size: 1.5rem;
  color: #64748b;
  cursor: pointer;
  transition: color 0.2s;
}

.btn-close-modal:hover {
  color: #1e293b;
}

/* Custom Dropdown no Modal */
.custom-select-wrapper {
  position: relative;
  width: 100%;
}

.custom-select-trigger {
  padding: 0.9rem 1.25rem 0.9rem 3.2rem;
  border: 1px solid var(--border-color, #e2e8f0);
  border-radius: 12px;
  font-family: "Inter", sans-serif;
  font-size: 0.95rem;
  background-color: var(--surface-color, #fafafa);
  color: var(--text-primary);
  transition: all 0.2s ease;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  user-select: none;
  position: relative;
}

.custom-select-trigger:hover {
  border-color: var(--primary-color, #111);
  background-color: var(--surface-color, #f5f5f7);
}

.custom-select-trigger--open {
  border-color: var(--primary-color, #111);
  background-color: var(--bg-color, white);
  box-shadow: 0 0 0 4px rgba(17, 17, 17, 0.05);
}

.select-trigger-icon {
  position: absolute;
  left: 1.25rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2rem;
  pointer-events: none;
}

.select-trigger-text {
  flex-grow: 1;
  text-align: left;
}

.select-trigger-caret {
  color: #64748b;
  font-size: 1rem;
  transition: transform 0.2s ease;
}

.custom-select-trigger--open .select-trigger-caret {
  transform: rotate(180deg);
}

.custom-options-menu {
  position: absolute;
  top: 105%;
  left: 0;
  right: 0;
  background: var(--bg-color, white);
  border: 1px solid var(--border-color, #e2e8f0);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  z-index: 1000;
  max-height: 200px;
  overflow-y: auto;
  scrollbar-width: thin;
  padding: 8px;
  margin-top: 4px;
}

.custom-option-item {
  padding: 10px 14px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.custom-option-item:hover {
  background-color: #f5f5f7;
}

.custom-option-item--selected {
  background-color: #f1f5f9;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.2s ease-out;
}
.slide-up-enter-from, .slide-up-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.btn-enviar-avaliacao {
  transition: all 0.3s ease;
}

.btn-enviar-avaliacao:hover:not(:disabled) {
  background: #222;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
}

.btn-xs {
  padding: 4px 10px;
  font-size: 0.75rem;
}

.fade-in-up {
  animation: fadeInUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
