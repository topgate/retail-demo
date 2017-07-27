import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { Product } from './models';
import { environment } from './../../environments/environment';

@Injectable()
export class StoreService {

  private storeCode = '000';

  constructor(private http: Http) { }

  getProducts(): Observable<Product[]> {
    return this.http.get(`${environment.apiHost}/api/1/product`, { params: { limit: 15 } }).map(resp => resp.json());
  }

  createOrder(product: Product, quantity = 1): Observable<any> {
    console.log(`createOrder: ${product.productCode}-${product.productName}`);
    return this.http.post(`${environment.apiHost}/api/1/order`, {
      storeCode: this.storeCode,
      productCode: product.productCode,
      quantity
    });
  }
}
