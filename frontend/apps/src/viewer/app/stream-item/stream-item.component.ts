import { Component, Input, HostBinding } from '@angular/core';
import { Receipt } from './../shared/models';
import { ReceiptWithKey } from './../shared/models';

@Component({
  selector: '[nxtStreamItem]',
  templateUrl: './stream-item.component.html',
  styleUrls: ['./stream-item.component.scss']
})
export class StreamItemComponent {

  @Input() data: ReceiptWithKey;

  @HostBinding('class.special') 
  get special() {
    return this.data.storeCode === '000';
  }

  constructor() { }
}
