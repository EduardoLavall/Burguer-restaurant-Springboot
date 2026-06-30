export type ModoApp = "livre" | "cliente" | "admin";

const modoConfigurado = import.meta.env.VITE_MODO_APP;

const modoApp: ModoApp =
  modoConfigurado === "cliente" || modoConfigurado === "admin" || modoConfigurado === "livre"
    ? modoConfigurado
    : "livre";

function podeAcessarCliente() {
  return modoApp === "livre" || modoApp === "cliente";
}

function podeAcessarAdmin() {
  return modoApp === "livre" || modoApp === "admin";
}

function deveMostrarMenu() {
  return modoApp === "livre";
}

function rotaInicial() {
  return podeAcessarCliente() ? "/cliente" : "/admin/produtos";
}

function rotaFallbackCliente() {
  return podeAcessarAdmin() ? "/admin/produtos" : "/cliente";
}

function rotaFallbackAdmin() {
  return podeAcessarCliente() ? "/cliente" : "/admin/produtos";
}

export {
  deveMostrarMenu,
  modoApp,
  podeAcessarAdmin,
  podeAcessarCliente,
  rotaFallbackAdmin,
  rotaFallbackCliente,
  rotaInicial,
};
