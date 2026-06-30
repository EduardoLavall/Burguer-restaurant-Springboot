import {
  Navigate,
  Outlet,
  createRootRoute,
  createRoute,
  createRouter,
} from "@tanstack/react-router";

import { AdminPedidosPage } from "../paginas/AdminPedidosPage";
import { ClienteCardapioPage } from "../paginas/ClienteCardapioPage";
import { PedidoConfirmacaoPage } from "../paginas/PedidoConfirmacaoPage";
import { ProdutosPage } from "../paginas/ProdutosPage";
import {
  podeAcessarAdmin,
  podeAcessarCliente,
  rotaFallbackAdmin,
  rotaFallbackCliente,
  rotaInicial,
} from "../config/modoApp";

function LayoutRotas() {
  return (
    <div className="shell-app">
      <header className="shell-app__topo">
        <div>
          <span className="shell-app__tag">Hamburgueria v1</span>
          <strong>Burguer Restaurant</strong>
        </div>
      </header>

      <Outlet />
    </div>
  );
}

const rootRoute = createRootRoute({
  component: LayoutRotas,
});

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/",
  component: () => <Navigate to={rotaInicial()} replace />,
});

const clienteRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/cliente",
  component: () =>
    podeAcessarCliente() ? <ClienteCardapioPage /> : <Navigate to={rotaFallbackCliente()} replace />,
});

const pedidoConfirmacaoRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/cliente/pedido/$pedidoId",
  component: () =>
    podeAcessarCliente() ? <PedidoConfirmacaoPage /> : <Navigate to={rotaFallbackCliente()} replace />,
});

const adminRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin",
  component: () => <Navigate to={rotaFallbackAdmin()} replace />,
});

const adminProdutosRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin/produtos",
  component: () =>
    podeAcessarAdmin() ? <ProdutosPage /> : <Navigate to={rotaFallbackAdmin()} replace />,
});

const adminPedidosRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin/pedidos",
  component: () =>
    podeAcessarAdmin() ? <AdminPedidosPage /> : <Navigate to={rotaFallbackAdmin()} replace />,
});

const routeTree = rootRoute.addChildren([
  homeRoute,
  clienteRoute,
  pedidoConfirmacaoRoute,
  adminRoute,
  adminProdutosRoute,
  adminPedidosRoute,
]);

const router = createRouter({
  routeTree,
  defaultPreload: "intent",
});

export { router };

declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}
