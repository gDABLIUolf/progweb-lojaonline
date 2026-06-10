<template>
  <div v-if="open" class="modal-overlay" @click="fechar">
    <div class="modal-content fade-in-up" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">Novo Produto</h3>

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

        <div class="mb-4">
          <label class="form-label text-muted small fw-bold">
            IMAGEM DO PRODUTO
          </label>

          <input
            type="file"
            class="form-control-premium"
            accept="image/*"
            multiple
            @change="capturarImagens"
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
          {{ salvando ? "Salvando..." : "Salvar Produto" }}
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
});

const emit = defineEmits(["update:open", "produto-salvo"]);

const estadoInicial = () => ({
  nome: "",
  descricao: "",
  preco: "",
  quantidadeEstoque: "",
  categoriasIds: [],
});

const novoProduto = ref(estadoInicial());

const arquivosImagens = ref([]);
const erroModal = ref("");
const salvando = ref(false);

const limparFormulario = () => {
  novoProduto.value = estadoInicial();
  arquivoImagem.value = null;
  erroModal.value = "";
};

const fechar = () => {
  emit("update:open", false);
  limparFormulario();
};

const capturarImagens = (event) => {
  arquivosImagens.value = Array.from(event.target.files).slice(0, 5);
};

const salvarProduto = async () => {
  if (novoProduto.value.categoriasIds.length === 0) {
    erroModal.value = "Selecione pelo menos uma categoria.";

    return;
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

    await api.post("/produtos", formData);

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

  z-index: 1000;
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
