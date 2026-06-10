<template>
  <div v-if="open" class="modal-overlay" @click="fechar">
    <div class="modal-content fade-in-up" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">{{ produtoParaEditar ? "Editar Produto" : "Novo Produto" }}</h3>

        <button @click="fechar" class="btn-close-modal">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <form @submit.prevent="salvarProduto">
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">
            NOME DO PRODUTO
          </label>

          <input
            type="text"
            v-model="novoProduto.nome"
            class="form-control-premium"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold"> DESCRIÇÃO </label>

          <textarea
            v-model="novoProduto.descricao"
            rows="3"
            class="form-control-premium"
            required
          />
        </div>

        <div class="row mb-3">
          <div class="col-6">
            <label class="form-label text-muted small fw-bold">
              PREÇO (R$)
            </label>

            <input
              type="number"
              step="0.01"
              v-model="novoProduto.preco"
              class="form-control-premium"
              required
            />
          </div>

          <div class="col-6">
            <label class="form-label text-muted small fw-bold"> ESTOQUE </label>

            <input
              type="number"
              v-model="novoProduto.quantidadeEstoque"
              class="form-control-premium"
              required
            />
          </div>
        </div>

        <!-- Desconto e Destaque Carrossel -->
        <div class="row mb-3 align-items-center">
          <div class="col-6">
            <label class="form-label text-muted small fw-bold"> DESCONTO (%) </label>
            <input
              type="number"
              min="0"
              max="100"
              v-model="novoProduto.desconto"
              class="form-control-premium"
            />
          </div>
          <div class="col-6 d-flex align-items-center">
            <div class="form-check mt-3">
              <input
                class="form-check-input"
                type="checkbox"
                id="destaqueCarrossel"
                v-model="novoProduto.destaqueCarrossel"
              />
              <label class="form-check-label fw-bold small text-muted ms-1" for="destaqueCarrossel">
                DESTAQUE CARROSSEL
              </label>
            </div>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">
            CATEGORIAS
          </label>

          <div class="categorias-box">
            <div
              class="form-check"
              v-for="categoria in categorias"
              :key="categoria.id"
            >
              <input
                class="form-check-input"
                type="checkbox"
                :id="`cat-${categoria.id}`"
                :value="categoria.id"
                v-model="novoProduto.categoriasIds"
              />

              <label class="form-check-label" :for="`cat-${categoria.id}`">
                {{ categoria.nome }}
              </label>
            </div>
          </div>

          <small v-if="categorias.length === 0" class="text-danger">
            Cadastre uma categoria primeiro.
          </small>
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">
            IMAGENS DO PRODUTO (1:1)
          </label>

          <input
            type="file"
            class="form-control-premium"
            accept="image/*"
            multiple
            @change="capturarImagens"
          />
        </div>

        <!-- Imagem do Carrossel Promocional -->
        <div class="mb-4" v-if="novoProduto.destaqueCarrossel">
          <label class="form-label text-muted small fw-bold">
            IMAGEM DE BANNER CARROSSEL (16:9)
          </label>

          <input
            type="file"
            class="form-control-premium"
            accept="image/*"
            @change="capturarImagemCarrossel"
          />
        </div>

        <div v-if="erroModal" class="alert alert-danger py-2 mb-3">
          {{ erroModal }}
        </div>

        <button
          type="submit"
          class="btn-premium w-100"
          :disabled="salvando || novoProduto.categoriasIds.length === 0"
        >
          {{ salvando ? "Salvando..." : (produtoParaEditar ? "Salvar Alterações" : "Salvar Produto") }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onUnmounted } from "vue";
import api from "../../services/api";

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  categorias: {
    type: Array,
    default: () => [],
  },
  produtoParaEditar: {
    type: Object,
    default: null,
  }
});

const emit = defineEmits(["update:open", "produto-salvo"]);

const estadoInicial = () => ({
  nome: "",
  descricao: "",
  preco: "",
  quantidadeEstoque: "",
  categoriasIds: [],
  desconto: 0,
  destaqueCarrossel: false,
});

const novoProduto = ref(estadoInicial());
const arquivosImagens = ref([]);
const arquivoImagemCarrossel = ref(null);
const erroModal = ref("");
const salvando = ref(false);

const limparFormulario = () => {
  novoProduto.value = estadoInicial();
  arquivosImagens.value = [];
  arquivoImagemCarrossel.value = null;
  erroModal.value = "";
};

const fechar = () => {
  emit("update:open", false);
  limparFormulario();
};

const capturarImagens = (event) => {
  arquivosImagens.value = Array.from(event.target.files).slice(0, 5);
};

const capturarImagemCarrossel = (event) => {
  arquivoImagemCarrossel.value = event.target.files[0];
};

const salvarProduto = async () => {
  if (novoProduto.value.categoriasIds.length === 0) {
    erroModal.value = "Selecione pelo menos uma categoria.";
    return;
  }

  // Validação: Pelo menos uma imagem do produto (1:1)
  const temImagemExistente = props.produtoParaEditar && props.produtoParaEditar.imagensIds && props.produtoParaEditar.imagensIds.length > 0;
  if (arquivosImagens.value.length === 0 && !temImagemExistente) {
    erroModal.value = "O produto deve possuir pelo menos uma imagem (proporção 1:1).";
    return;
  }

  // Validação: Imagem do carrossel (16:9) se destaqueCarrossel for verdadeiro
  if (novoProduto.value.destaqueCarrossel) {
    const temBannerExistente = props.produtoParaEditar && props.produtoParaEditar.temImagemCarrossel;
    if (!arquivoImagemCarrossel.value && !temBannerExistente) {
      erroModal.value = "Para destacar o produto no carrossel, é obrigatório enviar a imagem de banner (proporção 16:9).";
      return;
    }
  }

  salvando.value = true;
  erroModal.value = "";

  try {
    const formData = new FormData();

    formData.append(
      "dados",
      new Blob([JSON.stringify(novoProduto.value)], {
        type: "application/json",
      }),
    );

    arquivosImagens.value.forEach((file) => {
      formData.append("imagens", file);
    });

    if (arquivoImagemCarrossel.value) {
      formData.append("imagemCarrossel", arquivoImagemCarrossel.value);
    }

    if (props.produtoParaEditar) {
      await api.put(`/produtos/${props.produtoParaEditar.id}`, formData);
    } else {
      await api.post("/produtos", formData);
    }

    emit("produto-salvo");
    fechar();
  } catch (error) {
    console.error(error);
    erroModal.value = "Erro ao salvar produto.";
  } finally {
    salvando.value = false;
  }
};

watch(
  () => props.open,
  (aberto) => {
    document.body.style.overflow = aberto ? "hidden" : "";
    if (aberto) {
      if (props.produtoParaEditar) {
        const catIds = props.produtoParaEditar.categoriasNomes
          ? props.categorias
              .filter(c => props.produtoParaEditar.categoriasNomes.includes(c.nome))
              .map(c => c.id)
          : [];
        novoProduto.value = {
          nome: props.produtoParaEditar.nome,
          descricao: props.produtoParaEditar.descricao,
          preco: props.produtoParaEditar.preco,
          quantidadeEstoque: props.produtoParaEditar.quantidadeEstoque,
          categoriasIds: catIds,
          desconto: props.produtoParaEditar.desconto || 0,
          destaqueCarrossel: props.produtoParaEditar.destaqueCarrossel || false,
        };
      } else {
        novoProduto.value = estadoInicial();
      }
      arquivosImagens.value = [];
      arquivoImagemCarrossel.value = null;
      erroModal.value = "";
    }
  },
);

onUnmounted(() => {
  document.body.style.overflow = "";
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  z-index: 10500;
}

.modal-content {
  width: 100%;
  max-width: 550px;
  max-height: 90vh;
  overflow-y: auto;
  background: var(--bg-color);
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);
}

.btn-close-modal {
  border: none;
  background: transparent;
  font-size: 1.5rem;
  cursor: pointer;
}

.form-control-premium {
  width: 100%;
  padding: 1rem 1.2rem;
  border-radius: var(--radius-md);
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  transition: 0.3s;
}

.form-control-premium:focus {
  outline: none;
  border-color: var(--primary-color);
  background: white;
  box-shadow: 0 0 0 4px rgba(15, 23, 42, 0.05);
}

.categorias-box {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  padding: 1rem;
  border-radius: var(--radius-md);
  border: 1px solid #e2e8f0;
  background: #f8fafc;
}

.fade-in-up {
  animation: fadeInUp 0.25s ease;
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
