<template>
  <Transition name="toast-fade">
    <div v-if="toastState.visible" class="toast-wrapper">
      <div :class="['toast-card', `toast-${toastState.type}`]">
        <div class="toast-icon">
          <i v-if="toastState.type === 'error'" class="ph ph-warning-circle-fill"></i>
          <i v-else-if="toastState.type === 'success'" class="ph ph-check-circle-fill"></i>
          <i v-else-if="toastState.type === 'warning'" class="ph ph-warning-fill"></i>
          <i v-else class="ph ph-info-fill"></i>
        </div>
        <div class="toast-content">
          <span class="toast-message">{{ toastState.message }}</span>
        </div>
        <button class="toast-close" @click="hideToast" aria-label="Fechar">
          <i class="ph ph-x"></i>
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { toastState, hideToast } from "../../services/toast";
</script>

<style scoped>
.toast-wrapper {
  position: fixed;
  top: 45%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 11000;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
  width: 100%;
  padding: 0 24px;
}

.toast-card {
  pointer-events: auto;
  min-width: 420px;
  max-width: 650px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(16px) saturate(200%);
  -webkit-backdrop-filter: blur(16px) saturate(200%);
  border-radius: 24px;
  padding: 18px 26px;
  display: flex;
  align-items: center;
  gap: 18px;
  box-shadow: 
    0 20px 48px -10px rgba(0, 0, 0, 0.18),
    0 10px 24px -8px rgba(0, 0, 0, 0.08),
    inset 0 1px 1px rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.toast-icon {
  font-size: 2.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Tipos de Alerta - Cores Harmoniosas e Bordas de Destaque Maiores */
.toast-error {
  border-left: 8px solid #ef4444;
}
.toast-error .toast-icon {
  color: #ef4444;
}

.toast-success {
  border-left: 8px solid #10b981;
}
.toast-success .toast-icon {
  color: #10b981;
}

.toast-warning {
  border-left: 8px solid #f59e0b;
}
.toast-warning .toast-icon {
  color: #f59e0b;
}

.toast-info {
  border-left: 8px solid #3b82f6;
}
.toast-info .toast-icon {
  color: #3b82f6;
}

.toast-content {
  flex-grow: 1;
  text-align: left;
}

.toast-message {
  font-size: 1.1rem;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.5;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

.toast-close {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.3rem;
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  margin-left: 10px;
  flex-shrink: 0;
}

.toast-close:hover {
  background: rgba(0, 0, 0, 0.06);
  color: #1e293b;
}

/* ── Dark Mode: Toast mantém fundo claro para texto escuro sempre legível ── */
:global([data-theme="dark"]) .toast-card {
  background: rgba(248, 250, 252, 0.98) !important;
  border-color: rgba(0, 0, 0, 0.08) !important;
  box-shadow:
    0 20px 48px -10px rgba(0, 0, 0, 0.4),
    0 10px 24px -8px rgba(0, 0, 0, 0.2),
    inset 0 1px 1px rgba(255, 255, 255, 0.9);
}

:global([data-theme="dark"]) .toast-message {
  color: #0f172a !important;
}

:global([data-theme="dark"]) .toast-close {
  color: #64748b !important;
}

:global([data-theme="dark"]) .toast-close:hover {
  background: rgba(0, 0, 0, 0.06) !important;
  color: #1e293b !important;
}

/* Transições de Animação com Leve Efeito Bouncy */
.toast-fade-enter-active {
  animation: toast-in 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.toast-fade-leave-active {
  animation: toast-out 0.4s cubic-bezier(0.6, -0.28, 0.735, 0.045) forwards;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translate(-50%, -120%) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
  to {
    opacity: 0;
    transform: translate(-50%, -100%) scale(0.9);
  }
}
</style>
