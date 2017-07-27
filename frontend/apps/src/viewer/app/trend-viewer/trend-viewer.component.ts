import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TrendDataService } from './../shared/trend-data.service';

@Component({
  selector: '[nxtTrendViewer]',
  templateUrl: './trend-viewer.component.html',
  styleUrls: ['./trend-viewer.component.scss']
})
export class TrendViewerComponent implements OnInit {

  trend$: Observable<any>;

  constructor(private trendData: TrendDataService) { }

  ngOnInit() {
    this.trend$ = this.trendData.data$.map(trend => {
      const $medium = trend.reduce((sum, cur) => sum + cur.priceSum, 0) / trend.length;
      return trend.map(data => ({ $medium, ...data }));
    });
  }

  trackByFn(index, item) {
    return item.productCode;
  }

}
