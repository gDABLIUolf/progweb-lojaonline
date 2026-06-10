<template>
  <div v-if="open" class="modal-overlay" @click="fechar">
    <div class="modal-content fade-in-up" @click.stop>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0">{{ usuarioParaEditar ? "Editar Usuário" : "Novo Usuário" }}</h3>

        <button @click="fechar" class="btn-close-modal">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <form @submit.prevent="salvarUsuario">
        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">NOME COMPLETO</label>
          <input
            type="text"
            v-model="formDados.nome"
            class="form-control-premium"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">E-MAIL</label>
          <input
            type="email"
            v-model="formDados.email"
            class="form-control-premium"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">
            SENHA {{ usuarioParaEditar ? "(OPCIONAL)" : "" }}
          </label>
          <input
            type="password"
            v-model="formDados.senha"
            class="form-control-premium"
            :placeholder="usuarioParaEditar ? 'Mantenha em branco para não alterar' : 'Mínimo de 6 caracteres'"
            :required="!usuarioParaEditar"
          />
        </div>

        <div class="mb-3">
          <label class="form-label text-muted small fw-bold">PAPEL (ROLE)</label>
          <select v-model="formDados.role" class="form-control-premium" required>
            <option value="CLIENTE">Cliente</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>

        <div v-if="erroModal" class="alert alert-danger py-2 mb-3">
          {{ erroModal }}
        </div>

        <button type="submit" class="btn-premium w-100" :disabled="salvando">
          {{ salvando ? "Salvando..." : (usuarioParaEditar ? "Salvar Alterações" : "Salvar Usuário") }}
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
  usuarioParaEditar: {
    type: Object,
    default: null,
  }
});

const emit = defineEmits(["update:open", "usuario-salvo"]);

const estadoInicial = () => ({
  nome: "",
  email: "",
  senha: "",
  role: "CLIENTE",
});

const formDados = ref(estadoInicial());
const erroModal = ref("");
const salvando = ref(false);

const fechar = () => {
  emit("update:open", false);
  formDados.value = estadoInicial();
  erroModal.value = "";
};

const salvarUsuario = async () => {
  if (!props.usuarioParaEditar && formDados.value.senha.length < 6) {
    erroModal.value = "A senha deve ter pelo menos 6 caracteres.";
    return;
  }

  salvando.value = true;
  erroModal.value = "";

  try {
    const payload = {
      nome: formDados.value.nome,
      email: formDados.value.email,
      role: formDados.value.role,
    };

    if (formDados.value.senha) {
      payload.senha = formDados.value.senha;
    }

    if (props.usuarioParaEditar) {
      await api.put(`/usuarios/${props.usuarioParaEditar.id}`, payload);
    } else {
      await api.post("/usuarios", payload);
    }

    emit("usuario-salvo");
    fechar();
  } catch (error) {
    console.error(error);
    erroModal.value = error.response?.data || "Erro ao salvar usuário.";
  } finally {
    salvando.value = false;
  }
};

watch(
  () => props.open,
  (aberto) => {
    document.body.style.overflow = aberto ? "hidden" : "";
    if (aberto) {
      if (props.usuarioParaEditar) {
        formDados.value = {
          nome: props.usuarioParaEditar.nome,
          email: props.usuarioParaEditar.email,
          senha: "",
          role: props.usuarioParaEditar.role || "CLIENTE",
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
