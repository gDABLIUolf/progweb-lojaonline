<template>
  <div v-if="open" class="modal-overlay" @click="fechar">
    <div class="modal-content fade-in-up" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">Nova Categoria</h3>

        <button @click="fechar" class="btn-close-modal">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <form @submit.prevent="salvarCategoria">
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">
            NOME DA CATEGORIA
          </label>

          <input
            type="text"
            v-model="novaCategoria.nome"
            class="form-control-premium"
            placeholder="Ex: Moda Inverno"
            required
          />
        </div>

        <div class="mb-4">
          <label class="form-label text-muted small fw-bold">
            IMAGEM DE DESTAQUE
          </label>

          <input
            type="file"
            @change="capturarImagem"
            class="form-control-premium"
            accept="image/png,image/jpeg,image/webp"
            required
          />
        </div>

        <div v-if="erroModal" class="alert alert-danger py-2 mb-3">
          {{ erroModal }}
        </div>

        <button type="submit" class="btn-premium w-100" :disabled="salvando">
          {{ salvando ? "Salvando..." : "Salvar Categoria" }}
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
});

const emit = defineEmits(["update:open", "categoria-salva"]);

const novaCategoria = ref({
  nome: "",
});

const arquivoImagem = ref(null);

const erroModal = ref("");
const salvando = ref(false);

const fechar = () => {
  emit("update:open", false);

  novaCategoria.value = {
    nome: "",
  };

  arquivoImagem.value = null;
  erroModal.value = "";
};

const capturarImagem = (event) => {
  arquivoImagem.value = event.target.files[0];
};

const salvarCategoria = async () => {
  if (!arquivoImagem.value) {
    erroModal.value = "Selecione uma imagem.";
    return;
  }

  salvando.value = true;
  erroModal.value = "";

  try {
    const formData = new FormData();

    const jsonBlob = new Blob(
      [
        JSON.stringify({
          nome: novaCategoria.value.nome,
        }),
      ],
      {
        type: "application/json",
      },
    );

    formData.append("dados", jsonBlob);

    formData.append("imagem", arquivoImagem.value);

    await api.post("/categorias", formData);

    emit("categoria-salva");

    fechar();
  } catch (error) {
    console.error(error);

    erroModal.value = "Erro ao salvar categoria.";
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

  z-index: 10500;

  padding: 1rem;
}

.modal-content {
  width: 100%;
  max-width: 450px;

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
