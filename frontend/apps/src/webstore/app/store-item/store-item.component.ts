import { Component, Input, Output, ChangeDetectionStrategy, EventEmitter } from '@angular/core';
import { Product } from './../shared/models';

@Component({
  selector: 'nxt-store-item',
  templateUrl: './store-item.component.html',
  styleUrls: ['./store-item.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StoreItemComponent {

  @Input() product: Product;
  @Output() purchased = new EventEmitter();

  purchase() {
    this.purchased.emit();
  }
}
