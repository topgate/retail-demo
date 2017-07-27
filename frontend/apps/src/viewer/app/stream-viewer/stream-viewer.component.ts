import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, NgZone } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { StreamDataService } from './../shared/stream-data.service';
import { ReceiptWithKey } from './../shared/models';

@Component({
  selector: '[nxtStreamViewer]',
  templateUrl: './stream-viewer.component.html',
  styleUrls: ['./stream-viewer.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StreamViewerComponent implements OnInit, OnDestroy {

  private sub = new Subscription();
  stream: ReceiptWithKey[] = [];

  constructor(private cdRef: ChangeDetectorRef, private ngZone: NgZone, private streamData: StreamDataService) { }

  ngOnInit() {
    this.ngZone.runOutsideAngular(() => {
      // そのままの流速だと速すぎて描画が不安定になるので50msごとにitemが流れてくるようにdelayを挟んでいる
      const stream$ = this.streamData.data$.map(data => Observable.of(data).delay(20)).concatAll();

      this.sub.add(stream$.subscribe(item => {
        this.stream = [item, ...this.stream].slice(0, 100);
        this.cdRef.markForCheck();
      }));
    });
  }

  trackByFn(index: number, item: ReceiptWithKey) {
    return item.$key;
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}
