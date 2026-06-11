import { ref } from "vue";

export const toastState = ref({
  visible: false,
  message: "",
  type: "error", // "error" | "success" | "warning" | "info"
});

let timer = null;

export const showToast = (message, type = "error") => {
  if (timer) {
    clearTimeout(timer);
  }
  toastState.value = {
    visible: true,
    message,
    type,
  };
  timer = setTimeout(() => {
    toastState.value.visible = false;
  }, 7000);
};

export const hideToast = () => {
  toastState.value.visible = false;
  if (timer) {
    clearTimeout(timer);
    timer = null;
  }
};
