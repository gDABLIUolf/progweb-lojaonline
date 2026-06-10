<template>
  <div v-if="open" class="modal-overlay" @click="fechar">
    <div class="modal-content fade-in-up" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">{{ avaliacaoParaEditar ? "Editar Avaliação" : "Nova Avaliação" }}</h3>

        <button @click="fechar" class="btn-close-modal">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <form @submit.prevent="salvarAvaliacao">
        <!-- Produto (apenas para criação) -->
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">PRODUTO</label>
          <select 
            v-model="formDados.produtoId" 
            class="form-control-premium" 
            :disabled="!!avaliacaoParaEditar"
            required
          >
            <option value="" disabled>Selecione um produto</option>
            <option v-for="p in produtos" :key="p.id" :value="p.id">{{ p.nome }}</option>
          </select>
        </div>

        <!-- Usuário (apenas para criação) -->
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">USUÁRIO</label>
          <select 
            v-model="formDados.usuarioId" 
            class="form-control-premium" 
            :disabled="!!avaliacaoParaEditar"
            required
          >
            <option value="" disabled>Selecione um usuário</option>
            <option v-for="u in usuarios" :key="u.id" :value="u.id">{{ u.nome }} ({{ u.email }})</option>
          </select>
        </div>

        <!-- Nota -->
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">NOTA (0.5 a 5.0)</label>
          <select v-model="formDados.nota" class="form-control-premium" required>
            <option v-for="n in notas" :key="n" :value="n">{{ n }} Estrelas</option>
          </select>
        </div>

        <!-- Comentário -->
        <div class="mb-4">
          <label class="form-label text-muted small fw-bold">COMENTÁRIO</label>
          <textarea
            v-model="formDados.comentario"
            rows="3"
            class="form-control-premium"
            placeholder="Comentário sobre o produto"
            maxlength="500"
          ></textarea>
        </div>

        <div v-if="erroModal" class="alert alert-danger py-2 mb-3">
          {{ erroModal }}
        </div>

        <button type="submit" class="btn-premium w-100" :disabled="salvando">
          {{ salvando ? "Salvando..." : (avaliacaoParaEditar ? "Salvar Alterações" : "Salvar Avaliação") }}
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
  produtos: {
    type: Array,
    default: () => [],
  },
  usuarios: {
    type: Array,
    default: () => [],
  },
  avaliacaoParaEditar: {
    type: Object,
    default: null,
  }
});

const emit = defineEmits(["update:open", "avaliacao-salvo"]);

const notas = [0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0];

const estadoInicial = () => ({
  produtoId: "",
  usuarioId: "",
  nota: 5.0,
  comentario: "",
});

const formDados = ref(estadoInicial());
const erroModal = ref("");
const salvando = ref(false);

const fechar = () => {
  emit("update:open", false);
  formDados.value = estadoInicial();
  erroModal.value = "";
};

const salvarAvaliacao = async () => {
  salvando.value = true;
  erroModal.value = "";

  try {
    const payload = {
      nota: formDados.value.nota,
      comentario: formDados.value.comentario,
    };

    if (props.avaliacaoParaEditar) {
      await api.put(`/avaliacoes/${props.avaliacaoParaEditar.id}`, payload);
    } else {
      await api.post(`/avaliacoes/${formDados.value.usuarioId}/produto/${formDados.value.produtoId}`, payload);
    }

    emit("avaliacao-salvo");
    fechar();
  } catch (error) {
    console.error(error);
    erroModal.value = error.response?.data || "Erro ao salvar avaliação.";
  } finally {
    salvando.value = false;
  }
};

watch(
  () => props.open,
  (aberto) => {
    document.body.style.overflow = aberto ? "hidden" : "";
    if (aberto) {
      if (props.avaliacaoParaEditar) {
        // Encontrar produtoId e usuarioId pelos nomes/dados da avaliação se necessário, 
        // ou se a própria avaliação tiver esses dados.
        // No AvaliacaoResponseDTO temos nomeUsuario. No front podemos obter IDs, ou apenas deixar nulo porque está desativado.
        formDados.value = {
          produtoId: props.avaliacaoParaEditar.produtoId || "",
          usuarioId: props.avaliacaoParaEditar.usuarioId || "",
          nota: props.avaliacaoParaEditar.nota || 5.0,
          comentario: props.avaliacaoParaEditar.comentario || "",
        };
      } else {
        formDados.value = estadoInicial();
      }
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

.form-control-premium:disabled {
  background: #e2e8f0;
  cursor: not-allowed;
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
