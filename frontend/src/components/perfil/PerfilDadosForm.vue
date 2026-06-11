<template>
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
        <label for="perfil-nome" class="form-label text-muted small fw-bold">Nome Completo</label>
        <input
          type="text"
          id="perfil-nome"
          v-model="form.nome"
          class="form-control form-premium"
          required
        />
      </div>

      <div class="mb-3">
        <label for="perfil-email" class="form-label text-muted small fw-bold">E-mail</label>
        <input
          type="email"
          id="perfil-email"
          v-model="form.email"
          class="form-control form-premium"
          required
        />
      </div>

      <div class="mb-3">
        <label for="perfil-senha" class="form-label text-muted small fw-bold">Nova Senha (Opcional)</label>
        <input
          type="password"
          id="perfil-senha"
          v-model="form.senha"
          placeholder="Mínimo de 6 caracteres"
          class="form-control form-premium"
        />
      </div>

      <div class="mb-4">
        <label for="perfil-confirmar" class="form-label text-muted small fw-bold">Confirmar Senha</label>
        <input
          type="password"
          id="perfil-confirmar"
          v-model="form.confirmarSenha"
          placeholder="Repita a nova senha"
          class="form-control form-premium"
        />
      </div>

      <!-- Feedback -->
      <div
        v-if="feedbackMsg"
        :class="['alert', isFeedbackErro ? 'alert-danger' : 'alert-success', 'py-2', 'small', 'rounded-3']"
      >
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
</template>

<script setup>
import { ref, watch } from "vue";
import api from "../../services/api.js";

const props = defineProps({
  usuarioId: {
    type: [Number, String],
    required: true,
  },
  dadosUsuario: {
    type: Object,
    default: () => ({ nome: "", email: "" }),
  },
  isAdmin: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["salvo"]);

const form = ref({ nome: "", email: "", senha: "", confirmarSenha: "" });
const feedbackMsg = ref("");
const isFeedbackErro = ref(false);
const salvando = ref(false);

// Sincroniza o formulário quando os dados do usuário chegam do pai
watch(
  () => props.dadosUsuario,
  (dados) => {
    form.value.nome = dados.nome || "";
    form.value.email = dados.email || "";
    form.value.senha = "";
    form.value.confirmarSenha = "";
  },
  { immediate: true }
);

const salvarDados = async () => {
  feedbackMsg.value = "";
  isFeedbackErro.value = false;

  if (form.value.senha) {
    if (form.value.senha.length < 6) {
      feedbackMsg.value = "A nova senha deve ter pelo menos 6 caracteres.";
      isFeedbackErro.value = true;
      return;
    }
    if (form.value.senha !== form.value.confirmarSenha) {
      feedbackMsg.value = "A confirmação de senha não confere.";
      isFeedbackErro.value = true;
      return;
    }
  }

  try {
    salvando.value = true;
    const resposta = await api.put(`/usuarios/${props.usuarioId}`, {
      nome: form.value.nome,
      email: form.value.email,
      senha: form.value.senha || null,
    });

    feedbackMsg.value = "Dados atualizados com sucesso!";
    form.value.senha = "";
    form.value.confirmarSenha = "";
    emit("salvo", resposta.data);
  } catch (err) {
    feedbackMsg.value = err.response?.data || "Erro ao salvar alterações.";
    isFeedbackErro.value = true;
  } finally {
    salvando.value = false;
  }
};
</script>

<style scoped>
.perfil-card {
  background: var(--bg-color, white);
  border-radius: var(--radius-lg, 16px);
  border: 1px solid var(--border-color, rgba(0, 0, 0, 0.05)) !important;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  background: var(--surface-color, #f1f5f9);
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
</style>
