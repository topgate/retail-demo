import { Component, OnChanges, Input, ChangeDetectionStrategy, HostBinding } from '@angular/core';

@Component({
  selector: '[nxtTrendItem]',
  templateUrl: './trend-item.component.html',
  styleUrls: ['./trend-item.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrendItemComponent implements OnChanges {
  @Input() item: {
    $medium: number;

    productCode: string;
    productName: string;
    priceSum: number;
    quantitySum: number;
    categoryId: string;
    categoryName: string;
  };

  @Input() rank: number;

  ratio: number;

  get cardClass() {
    return [`trend-rank-${this.rank}`, `category--${this.item.categoryId}`];
  }

  @HostBinding('style.width')
  get hostWidth() {
    return `${this.ratio * 30}%`;
  }
  @HostBinding('style.min-width')
  get hostMinWidth() {
    return `20%`;
  }
  @HostBinding('style.max-width')
  get hostMaxWidth() {
    return `40%`;
  }
  @HostBinding('style.font-size')
  get hostFontSize() {
    return `${Math.min(Math.max(this.ratio, 1), 3.0)}em`;
  }

  ngOnChanges(changes) {
    this.ratio = Math.pow(this.item.priceSum / this.item.$medium, 1/ this.rank);
  }
}
