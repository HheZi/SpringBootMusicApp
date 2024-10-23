import { RouteReuseStrategy, DetachedRouteHandle, ActivatedRouteSnapshot } from '@angular/router';

export class CustomReuseStrategy implements RouteReuseStrategy {
  private storedRoutes: { [key: string]: DetachedRouteHandle } = {};

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    console.log('Detach route:', route.routeConfig?.path);
    return route.routeConfig?.path === ''; 
  }
  
  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle): void {
    console.log('Storing:', route.routeConfig?.path);
    this.storedRoutes[route.routeConfig!.path!] = handle;
  }
  
  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    console.log('Attach route:', route.routeConfig?.path);
    return !!this.storedRoutes[route.routeConfig!.path!];
  }
  
  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    console.log('Retrieve route:', route.routeConfig?.path);
    return this.storedRoutes[route.routeConfig!.path!] || null;
  }
  
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    console.log('Reuse route:', future.routeConfig?.path === curr.routeConfig?.path);
    return future.routeConfig === curr.routeConfig;
  }
  
}
