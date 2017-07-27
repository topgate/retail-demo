import { Component, OnInit } from '@angular/core';
import { MdSnackBar } from '@angular/material';
import { Observable } from 'rxjs/Observable';
import { StoreService } from './shared/store.service';
import { Product } from './shared/models';
import { StoreOrderedMessageComponent } from './store-ordered-message/store-ordered-message.component';

@Component({
  selector: 'nxt-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  products$: Observable<Product[]>;

  constructor(private storeService: StoreService, private snackBar: MdSnackBar) { }

  ngOnInit() {
    this.products$ = this.storeService.getProducts().do(list => console.log(list));
  }

  onPurchased(product: Product) {
    this.storeService.createOrder(product).subscribe(() => {
      this.snackBar.openFromComponent(StoreOrderedMessageComponent, {
        duration: 2000,
      });
    });
  }
}
