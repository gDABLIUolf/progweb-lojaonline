<template>
  <div class="checkout-wrapper">
    <!-- Navbar Simples / Seguro -->
    <nav class="navbar shadow-sm bg-white py-3 mb-4">
      <div class="container d-flex justify-content-between align-items-center">
        <RouterLink to="/" class="logo text-dark text-decoration-none fs-4 fw-bold">
          vesteBem<span class="dot">.</span>
        </RouterLink>
        <span class="text-muted d-flex align-items-center gap-2" style="font-size: 0.95rem">
          <i class="ph ph-lock-keyhole text-success fs-5"></i> Ambiente Seguro
        </span>
      </div>
    </nav>

    <div class="container py-4">
      <!-- Caso não esteja logado -->
      <div v-if="!estaLogado" class="text-center py-5 fade-in-up card p-5 shadow-sm border-0">
        <i class="ph ph-lock-keyhole" style="font-size: 4rem; color: var(--text-secondary)"></i>
        <h3 class="mt-3 fw-bold">Faça login para continuar</h3>
        <p class="text-muted mb-4">É necessário estar autenticado para finalizar a sua compra.</p>
        <RouterLink to="/login" class="btn-premium px-5 py-3 text-decoration-none">Entrar na Minha Conta</RouterLink>
      </div>

      <!-- Caso o carrinho ou itens de checkout estejam vazios e não tenha finalizado -->
      <div v-else-if="itens.length === 0 && !sucesso" class="text-center py-5 fade-in-up card p-5 shadow-sm border-0">
        <i class="ph ph-shopping-cart" style="font-size: 4rem; color: var(--text-secondary)"></i>
        <h3 class="mt-3 fw-bold">Nenhum produto para finalizar</h3>
        <p class="text-muted mb-4">O seu carrinho ou seleção de compra está vazio.</p>
        <RouterLink to="/" class="btn-premium px-5 py-3 text-decoration-none">Ir para a Loja</RouterLink>
      </div>

      <!-- Tela de Sucesso -->
      <div v-else-if="sucesso" class="row justify-content-center py-4 fade-in-up">
        <div class="col-lg-7 text-center">
          <div class="card p-5 shadow-sm border-0 rounded-4 bg-white text-center">
            <div class="success-animation mb-4">
              <div class="success-icon">
                <i class="ph ph-check-circle-fill"></i>
              </div>
            </div>
            <h2 class="fw-bold mb-2">Pedido Realizado!</h2>
            <p class="text-muted mb-4 fs-5">
              Sua compra de <strong class="text-dark">R$ {{ totalPedido.toFixed(2) }}</strong> foi processada com sucesso.
            </p>

            <div class="info-pagamento-sucesso p-4 rounded-4 mb-4 text-start">
              <h6 class="fw-bold border-bottom pb-2 mb-3">Resumo do Pagamento (Simulado)</h6>
              <div class="d-flex justify-content-between mb-2 small">
                <span class="text-muted">Método Escolhido:</span>
                <span class="fw-semibold text-capitalize">{{ metodo }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2 small">
                <span class="text-muted">Status do Pagamento:</span>
                <span class="badge" :class="(metodo === 'cartao' || metodo === 'pix') ? 'bg-success' : 'bg-warning text-dark'">
                  {{ metodo === 'cartao' ? 'Aprovado' : (metodo === 'pix' ? 'Concluído' : 'Aguardando Pagamento') }}
                </span>
              </div>
              
              <!-- Detalhes Extras conforme Método -->
              <div v-if="metodo === 'pix'" class="mt-3 bg-white p-3 rounded-3 border">
                <p class="small text-muted mb-2">Utilize a chave copia e cola abaixo no app do seu banco para pagar:</p>
                <div class="d-flex gap-2">
                  <input type="text" readonly class="form-control form-control-sm text-truncate bg-light" :value="chavePix" />
                  <button class="btn btn-outline-dark btn-sm text-nowrap" @click="copiarTexto(chavePix)">
                    <i class="ph ph-copy me-1"></i>Copiar
                  </button>
                </div>
              </div>

              <div v-if="metodo === 'boleto'" class="mt-3 bg-white p-3 rounded-3 border">
                <p class="small text-muted mb-2">Linha digitável do boleto gerado:</p>
                <div class="d-flex gap-2">
                  <input type="text" readonly class="form-control form-control-sm text-truncate bg-light" :value="codigoBarras" />
                  <button class="btn btn-outline-dark btn-sm text-nowrap" @click="copiarTexto(codigoBarras)">
                    <i class="ph ph-copy me-1"></i>Copiar
                  </button>
                </div>
              </div>
            </div>

            <div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
              <RouterLink to="/perfil" class="btn-premium py-3 px-4 text-decoration-none">
                <i class="ph ph-list-bullets me-2"></i>Ver Meus Pedidos
              </RouterLink>
              <RouterLink to="/" class="btn btn-outline-dark btn-premium-outline py-3 px-4 rounded-pill">
                <i class="ph ph-shopping-bag me-2"></i>Continuar Comprando
              </RouterLink>
            </div>
          </div>
        </div>
      </div>

      <!-- Layout Principal do Checkout -->
      <div v-else class="row g-4">
        <!-- Coluna Esquerda: Métodos e Formulários -->
        <div class="col-lg-7">
          <div class="card p-4 rounded-4 shadow-sm border-0 bg-white">
            <h4 class="fw-bold mb-4">Escolha a Forma de Pagamento</h4>

            <!-- Seletor de Métodos -->
            <div class="row g-2 mb-4 selector-pagamento">
              <div class="col-4">
                <button
                  type="button"
                  class="btn-metodo w-100 py-3"
                  :class="{ 'btn-metodo--selected': metodo === 'pix' }"
                  @click="selecionarMetodo('pix')"
                >
                  <i class="ph ph-qr-code fs-3 mb-1"></i>
                  <span>Pix</span>
                </button>
              </div>
              <div class="col-4">
                <button
                  type="button"
                  class="btn-metodo w-100 py-3"
                  :class="{ 'btn-metodo--selected': metodo === 'cartao' }"
                  @click="selecionarMetodo('cartao')"
                >
                  <i class="ph ph-credit-card fs-3 mb-1"></i>
                  <span>Cartão</span>
                </button>
              </div>
              <div class="col-4">
                <button
                  type="button"
                  class="btn-metodo w-100 py-3"
                  :class="{ 'btn-metodo--selected': metodo === 'boleto' }"
                  @click="selecionarMetodo('boleto')"
                >
                  <i class="ph ph-barcode fs-3 mb-1"></i>
                  <span>Boleto</span>
                </button>
              </div>
            </div>

            <hr class="mb-4" />

            <!-- FORMULÁRIO PIX -->
            <div v-if="metodo === 'pix'" class="fade-in">
              <div class="alert alert-light border rounded-3 d-flex align-items-start gap-3 mb-4">
                <i class="ph ph-info text-dark fs-4 mt-1"></i>
                <div class="small">
                  <strong>Pagamento instantâneo via Pix:</strong> Insira os seus dados de conferência abaixo para finalizar o pedido. O pagamento será processado imediatamente após confirmação.
                </div>
              </div>

              <!-- Form Checks -->
              <form @submit.prevent="confirmarPagamento">
                <div class="row g-3">
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">Nome Completo</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-user"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.nome }"
                        placeholder="Nome completo do pagador"
                        v-model="form.nome"
                        @input="limparErro('nome')"
                      />
                      <div class="invalid-feedback" v-if="erros.nome">{{ erros.nome }}</div>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">CPF</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-identification-card"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cpf }"
                        placeholder="000.000.000-00"
                        v-model="form.cpf"
                        @input="aplicarMascaraCPF"
                      />
                      <div class="invalid-feedback" v-if="erros.cpf">{{ erros.cpf }}</div>
                    </div>
                  </div>
                </div>

                <div class="mt-4">
                  <button
                    type="submit"
                    class="btn-premium w-100 py-3"
                    :disabled="finalizando"
                  >
                    <span v-if="finalizando" class="spinner-border spinner-border-sm me-2"></span>
                    {{ finalizando ? 'Processando...' : 'Confirmar e Finalizar Pix' }}
                  </button>
                </div>
              </form>
            </div>

            <!-- FORMULÁRIO CARTÃO -->
            <div v-if="metodo === 'cartao'" class="fade-in">
              <!-- Form Checks -->
              <form @submit.prevent="confirmarPagamento">
                <div class="row g-3">
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">Número do Cartão</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-credit-card"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cartaoNumero }"
                        placeholder="0000 0000 0000 0000"
                        v-model="form.cartaoNumero"
                        @input="aplicarMascaraCartao"
                      />
                      <div class="invalid-feedback" v-if="erros.cartaoNumero">{{ erros.cartaoNumero }}</div>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">Nome Impresso no Cartão</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-user"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cartaoNome }"
                        placeholder="NOME COMPLETO COMO NO CARTÃO"
                        v-model="form.cartaoNome"
                        @input="limparErro('cartaoNome')"
                      />
                      <div class="invalid-feedback" v-if="erros.cartaoNome">{{ erros.cartaoNome }}</div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-bold">Data de Validade</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-calendar-blank"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cartaoValidade }"
                        placeholder="MM/AA"
                        v-model="form.cartaoValidade"
                        @input="aplicarMascaraValidade"
                      />
                      <div class="invalid-feedback" v-if="erros.cartaoValidade">{{ erros.cartaoValidade }}</div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-bold">Código de Segurança (CVV)</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-password"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cartaoCvv }"
                        placeholder="123"
                        v-model="form.cartaoCvv"
                        @input="aplicarMascaraCvv"
                        maxlength="3"
                      />
                      <div class="invalid-feedback" v-if="erros.cartaoCvv">{{ erros.cartaoCvv }}</div>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">CPF do Titular</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-identification-card"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.cartaoCpf }"
                        placeholder="000.000.000-00"
                        v-model="form.cartaoCpf"
                        @input="aplicarMascaraCartaoCPF"
                      />
                      <div class="invalid-feedback" v-if="erros.cartaoCpf">{{ erros.cartaoCpf }}</div>
                    </div>
                  </div>
                </div>

                <div class="mt-4">
                  <button
                    type="submit"
                    class="btn-premium w-100 py-3"
                    :disabled="finalizando"
                  >
                    <span v-if="finalizando" class="spinner-border spinner-border-sm me-2"></span>
                    {{ finalizando ? 'Processando...' : 'Finalizar Compra com Cartão' }}
                  </button>
                </div>
              </form>
            </div>

            <!-- FORMULÁRIO BOLETO -->
            <div v-if="metodo === 'boleto'" class="fade-in">
              <div class="alert alert-light border rounded-3 d-flex align-items-start gap-3 mb-4">
                <i class="ph ph-info text-dark fs-4 mt-1"></i>
                <div class="small">
                  <strong>Geração de Boleto:</strong> Insira os seus dados de conferência abaixo para gerar o boleto. O boleto bancário simulado será enviado por e-mail e exibido após confirmação.
                </div>
              </div>

              <!-- Form Checks -->
              <form @submit.prevent="confirmarPagamento">
                <div class="row g-3">
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">Nome Completo</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-user"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.boletoNome }"
                        placeholder="Nome completo do pagador"
                        v-model="form.boletoNome"
                        @input="limparErro('boletoNome')"
                      />
                      <div class="invalid-feedback" v-if="erros.boletoNome">{{ erros.boletoNome }}</div>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">CPF</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-identification-card"></i>
                      <input
                        type="text"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.boletoCpf }"
                        placeholder="000.000.000-00"
                        v-model="form.boletoCpf"
                        @input="aplicarMascaraBoletoCPF"
                      />
                      <div class="invalid-feedback" v-if="erros.boletoCpf">{{ erros.boletoCpf }}</div>
                    </div>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-bold">E-mail para Envio</label>
                    <div class="input-icon-wrapper">
                      <i class="ph ph-envelope"></i>
                      <input
                        type="email"
                        class="form-control form-control-custom"
                        :class="{ 'is-invalid': erros.boletoEmail }"
                        placeholder="seu-email@provedor.com"
                        v-model="form.boletoEmail"
                        @input="limparErro('boletoEmail')"
                      />
                      <div class="invalid-feedback" v-if="erros.boletoEmail">{{ erros.boletoEmail }}</div>
                    </div>
                  </div>
                </div>

                <div class="mt-4">
                  <button
                    type="submit"
                    class="btn-premium w-100 py-3"
                    :disabled="finalizando"
                  >
                    <span v-if="finalizando" class="spinner-border spinner-border-sm me-2"></span>
                    {{ finalizando ? 'Processando...' : 'Gerar e Finalizar Boleto' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Coluna Direita: Resumo do Pedido -->
        <div class="col-lg-5">
          <div class="card p-4 rounded-4 shadow-sm border-0 bg-white sticky-summary">
            <h4 class="fw-bold mb-4">Resumo da Compra</h4>

            <!-- Listagem de Itens -->
            <div class="lista-itens-checkout mb-4">
              <div
                v-for="item in itens"
                :key="item.itemId"
                class="d-flex align-items-center gap-3 py-3 border-bottom"
              >
                <img
                  :src="`http://localhost:8080/api/produtos/${item.produtoId}/imagem`"
                  :alt="item.produtoNome"
                  class="produto-img-checkout rounded-3 border"
                />
                <div class="flex-grow-1">
                  <h6 class="fw-bold mb-0 text-truncate" style="max-width: 180px">{{ item.produtoNome }}</h6>
                  <span class="text-muted small">Qtd: {{ item.quantidade }}</span>
                </div>
                <div class="text-end">
                  <span class="fw-bold text-dark">R$ {{ item.precoTotal.toFixed(2) }}</span>
                </div>
              </div>
            </div>

            <!-- Valores Finais -->
            <div class="d-flex justify-content-between mb-2">
              <span class="text-muted">Subtotal</span>
              <span class="fw-semibold">R$ {{ totalPedido.toFixed(2) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Frete</span>
              <span class="text-success fw-bold">Grátis</span>
            </div>

            <hr class="my-3" />

            <div class="d-flex justify-content-between align-items-center mb-4">
              <span class="fw-bold fs-5 text-dark">Total</span>
              <span class="fw-bold fs-4 text-dark">R$ {{ totalPedido.toFixed(2) }}</span>
            </div>

            <RouterLink
              to="/carrinho"
              class="btn btn-outline-dark w-100 py-3 rounded-pill d-flex align-items-center justify-content-center gap-2"
              style="border-color: var(--border-color); font-weight: 500;"
            >
              <i class="ph ph-arrow-left"></i> Voltar ao Carrinho
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
const usuarioId = ref(null);
const itens = ref([]);
const totalPedido = ref(0);

const metodo = ref("pix"); // pix, cartao, boleto
const finalizando = ref(false);
const sucesso = ref(false);

// Estados de geração simulada
const pixGerado = ref(false);
const boletoGerado = ref(false);
const chavePix = ref("");
const codigoBarras = ref("");

// Dados do formulário
const form = ref({
  nome: "",
  cpf: "",
  cartaoNumero: "",
  cartaoNome: "",
  cartaoValidade: "",
  cartaoCvv: "",
  cartaoCpf: "",
  boletoNome: "",
  boletoCpf: "",
  boletoEmail: ""
});

// Erros de validação
const erros = ref({});

// ─── Inicialização ─────────────────────────────────────────────────────────────
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

onMounted(() => {
  const token = localStorage.getItem("token_vestebem");
  if (token) {
    estaLogado.value = true;
    usuarioId.value = obterUsuarioId();
    
    // Carrega itens salvos no localStorage
    const saved = localStorage.getItem("vestebem_checkout_itens");
    if (saved) {
      try {
        itens.value = JSON.parse(saved);
        totalPedido.value = itens.value.reduce((acc, i) => acc + i.precoTotal, 0);
      } catch {
        itens.value = [];
      }
    }
  } else {
    estaLogado.value = false;
  }
});

// Seleção de métodos
const selecionarMetodo = (m) => {
  metodo.value = m;
  erros.value = {};
  pixGerado.value = false;
  boletoGerado.value = false;
};

// ─── Máscaras de Input ─────────────────────────────────────────────────────────
const formatarCPF = (valor) => {
  let v = valor.replace(/\D/g, "");
  if (v.length > 11) v = v.slice(0, 11);
  if (v.length > 9) {
    v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2})$/, "$1.$2.$3-$4");
  } else if (v.length > 6) {
    v = v.replace(/^(\d{3})(\d{3})(\d{1,3})$/, "$1.$2.$3");
  } else if (v.length > 3) {
    v = v.replace(/^(\d{3})(\d{1,3})$/, "$1.$2");
  }
  return v;
};

const aplicarMascaraCPF = () => {
  form.value.cpf = formatarCPF(form.value.cpf);
  limparErro("cpf");
};

const aplicarMascaraCartaoCPF = () => {
  form.value.cartaoCpf = formatarCPF(form.value.cartaoCpf);
  limparErro("cartaoCpf");
};

const aplicarMascaraBoletoCPF = () => {
  form.value.boletoCpf = formatarCPF(form.value.boletoCpf);
  limparErro("boletoCpf");
};

const aplicarMascaraCartao = () => {
  let v = form.value.cartaoNumero.replace(/\D/g, "");
  if (v.length > 16) v = v.slice(0, 16);
  v = v.replace(/(\d{4})(?=\d)/g, "$1 ");
  form.value.cartaoNumero = v;
  limparErro("cartaoNumero");
};

const aplicarMascaraValidade = () => {
  let v = form.value.cartaoValidade.replace(/\D/g, "");
  if (v.length > 4) v = v.slice(0, 4);
  if (v.length > 2) {
    v = v.substring(0, 2) + "/" + v.substring(2);
  }
  form.value.cartaoValidade = v;
  limparErro("cartaoValidade");
};

const aplicarMascaraCvv = () => {
  let v = form.value.cartaoCvv.replace(/\D/g, "");
  if (v.length > 3) v = v.slice(0, 3);
  form.value.cartaoCvv = v;
  limparErro("cartaoCvv");
};

const limparErro = (campo) => {
  if (erros.value[campo]) {
    delete erros.value[campo];
  }
};

// ─── Validações de Negócio ─────────────────────────────────────────────────────
const validarNome = (nome) => {
  if (!nome || nome.trim().length < 3) {
    return "O nome completo deve conter pelo menos 3 caracteres.";
  }
  // Permitir apenas letras e espaços
  if (!/^[A-Za-zÀ-ÖØ-öø-ÿ\s]+$/.test(nome.trim())) {
    return "O nome deve conter apenas letras e espaços.";
  }
  return null;
};

const validarCPFValido = (cpfRaw) => {
  const cpf = cpfRaw.replace(/\D/g, "");
  if (cpf.length !== 11) {
    return "O CPF deve conter exatamente 11 dígitos.";
  }
  return null;
};

const validarEmail = (email) => {
  if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return "Por favor, insira um e-mail válido.";
  }
  return null;
};

// ─── Submissão / Confirmação do Pagamento ──────────────────────────────────────
const confirmarPagamento = () => {
  erros.value = {};

  if (metodo.value === "pix") {
    const erroNome = validarNome(form.value.nome);
    const erroCpf = validarCPFValido(form.value.cpf);

    if (erroNome) erros.value.nome = erroNome;
    if (erroCpf) erros.value.cpf = erroCpf;

    if (Object.keys(erros.value).length === 0) {
      const aleatorio = Math.random().toString(36).substring(2, 18).toUpperCase();
      chavePix.value = `00020101021226840014br.gov.bcb.pix25620025vestebem.com.br/pix/${aleatorio}5204000053039865405${totalPedido.value.toFixed(2)}5802BR5908vesteBem6009SAO_PAULO62070503***6304`;
      concluirPedido();
    }
  } 
  
  else if (metodo.value === "cartao") {
    const cardDigits = form.value.cartaoNumero.replace(/\D/g, "");
    if (cardDigits.length !== 16) {
      erros.value.cartaoNumero = "O cartão de crédito deve possuir 16 dígitos.";
    }

    const erroNome = validarNome(form.value.cartaoNome);
    if (erroNome) erros.value.cartaoNome = erroNome;

    const validade = form.value.cartaoValidade.trim();
    if (!/^\d{2}\/\d{2}$/.test(validade)) {
      erros.value.cartaoValidade = "Formato inválido (MM/AA).";
    } else {
      const parts = validade.split("/");
      const mes = parseInt(parts[0]);
      const ano = parseInt("20" + parts[1]);
      const hoje = new Date();
      const anoHoje = hoje.getFullYear();
      const mesHoje = hoje.getMonth() + 1; // 1-12

      if (mes < 1 || mes > 12) {
        erros.value.cartaoValidade = "Mês deve ser entre 01 e 12.";
      } else if (ano < anoHoje || (ano === anoHoje && mes < mesHoje)) {
        erros.value.cartaoValidade = "Cartão com validade expirada.";
      }
    }

    const cvvDigits = form.value.cartaoCvv.replace(/\D/g, "");
    if (cvvDigits.length !== 3) {
      erros.value.cartaoCvv = "CVV deve possuir exatamente 3 dígitos numéricos.";
    }

    const erroCpf = validarCPFValido(form.value.cartaoCpf);
    if (erroCpf) erros.value.cartaoCpf = erroCpf;

    if (Object.keys(erros.value).length === 0) {
      concluirPedido();
    }
  } 
  
  else if (metodo.value === "boleto") {
    const erroNome = validarNome(form.value.boletoNome);
    const erroCpf = validarCPFValido(form.value.boletoCpf);
    const erroEmail = validarEmail(form.value.boletoEmail);

    if (erroNome) erros.value.boletoNome = erroNome;
    if (erroCpf) erros.value.boletoCpf = erroCpf;
    if (erroEmail) erros.value.boletoEmail = erroEmail;

    if (Object.keys(erros.value).length === 0) {
      const blBlock1 = Math.floor(Math.random() * 90000) + 10000;
      const blBlock2 = Math.floor(Math.random() * 90000) + 10000;
      const blBlock3 = Math.floor(Math.random() * 90000) + 10000;
      const blBlock4 = Math.floor(Math.random() * 90000) + 10000;
      codigoBarras.value = `34191.${blBlock1} ${blBlock2}.${blBlock3} ${blBlock4}.120002 9 84560000${totalPedido.value.toFixed(2).replace(/\D/g, "")}`;
      concluirPedido();
    }
  }
};

// ─── Concluir Compra (API Integrada) ───────────────────────────────────────────
const concluirPedido = async () => {
  if (itens.value.length === 0 || !usuarioId.value) return;

  finalizando.value = true;
  try {
    // 1. Monta payload para realizar checkout oficial no banco de dados
    const itensPayload = itens.value.map((item) => ({
      produtoId: item.produtoId,
      quantidade: item.quantidade,
    }));

    // Cria o pedido no backend
    await api.post("/pedidos/checkout", {
      usuarioId: usuarioId.value,
      itens: itensPayload,
    });

    // 2. Remove do carrinho do banco de dados todos os itens pedidos
    await Promise.all(
      itens.value.map((item) =>
        api.delete(`/carrinhos/${usuarioId.value}/remover-tudo/${item.produtoId}`)
      )
    );

    // 3. Remove a seleção do localStorage e ativa tela de sucesso
    localStorage.removeItem("vestebem_checkout_itens");
    sucesso.value = true;
  } catch (error) {
    alert(error.response?.data || "Ocorreu um erro ao registrar seu pedido. Tente novamente.");
  } finally {
    finalizando.value = false;
  }
};

// Helper para copiar textos
const copiarTexto = (texto) => {
  navigator.clipboard.writeText(texto).then(() => {
    alert("Código copiado para a área de transferência!");
  }).catch(() => {
    alert("Falha ao copiar. Selecione e copie manualmente.");
  });
};
</script>

<style scoped>
.checkout-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color);
  padding-bottom: 3rem;
}

.dot {
  color: var(--text-secondary);
}

.logo {
  font-family: "Inter", sans-serif;
  letter-spacing: -0.5px;
}

/* ── Métodos de pagamento ── */
.btn-metodo {
  background: white;
  border: 2px solid #e5e5ea;
  border-radius: var(--radius-sm);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  font-weight: 600;
  font-size: 0.9rem;
  transition: var(--transition);
  cursor: pointer;
}

.btn-metodo:hover {
  border-color: var(--primary-color);
  background: #fbfbfd;
}

.btn-metodo--selected {
  border-color: var(--primary-color);
  background: var(--primary-color);
  color: white;
}

.btn-metodo--selected:hover {
  background: var(--primary-color);
  color: white;
}

/* ── Custom Inputs ── */
.input-icon-wrapper {
  position: relative;
  width: 100%;
}

.input-icon-wrapper i {
  position: absolute;
  left: 1.25rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary);
  font-size: 1.2rem;
  pointer-events: none;
}

.form-control-custom {
  padding: 0.9rem 1rem 0.9rem 3.2rem;
  border: 1px solid var(--border-color);
  border-radius: 999px;
  font-family: "Inter", sans-serif;
  font-size: 0.95rem;
  background-color: #fbfbfd;
  transition: var(--transition);
  width: 100%;
}

.form-control-custom:focus {
  border-color: var(--primary-color);
  background-color: white;
  outline: none;
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.05);
}

.form-control-custom.is-invalid {
  border-color: #dc3545;
  background-image: none;
}

.invalid-feedback {
  margin-left: 1.5rem;
  font-size: 0.8rem;
  font-weight: 500;
}

/* ── Cartão Virtual Preview ── */
.credit-card-preview {
  background: linear-gradient(135deg, #1d1d1f 0%, #3e3e42 100%);
  border-radius: var(--radius-md);
  position: relative;
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
  max-width: 380px;
  margin: 0 auto;
}

.credit-card-preview::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -20%;
  width: 100%;
  height: 120%;
  background: rgba(255, 255, 255, 0.03);
  transform: rotate(30deg);
  pointer-events: none;
}

.small-label {
  font-size: 0.65rem;
  text-transform: uppercase;
  color: #86868b;
  letter-spacing: 0.5px;
  margin-bottom: 2px;
}

.card-number-display {
  letter-spacing: 2px;
}

/* ── Resumo Direita ── */
.sticky-summary {
  position: sticky;
  top: 90px;
}

.produto-img-checkout {
  width: 56px;
  height: 56px;
  object-fit: cover;
  flex-shrink: 0;
}

.lista-itens-checkout {
  max-height: 250px;
  overflow-y: auto;
  scrollbar-width: thin;
}

/* ── Sucesso ── */
.success-animation {
  display: flex;
  justify-content: center;
}

.success-icon {
  font-size: 5rem;
  color: #34c759;
  animation: scaleUp 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
}

.info-pagamento-sucesso {
  background-color: #f5f5f7;
  border: 1px solid #e5e5ea;
}

.btn-premium-outline:hover {
  background-color: #1a1a1a;
  color: white;
}

/* ── Barcode ── */
.barcode-preview {
  letter-spacing: 1px;
}

.bar-thin {
  width: 1px;
  background-color: #1a1a1a;
}
.bar-thick {
  width: 4px;
  background-color: #1a1a1a;
}
.bar-space {
  width: 2px;
  background-color: transparent;
}

@keyframes scaleUp {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.fade-in {
  animation: fadeIn 0.4s ease forwards;
}

.fade-in-up {
  animation: fadeIn 0.6s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}
</style>
