import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { AngularFireDatabase } from 'angularfire2/database';
import { environment } from './../../environments/environment';
import { ReceiptWithKey } from './models';

@Injectable()
export class StreamDataService implements OnDestroy {

  private _data$: Observable<ReceiptWithKey>;

  constructor(private db: AngularFireDatabase, private ngZone: NgZone) {
    this._data$ = new Observable(observer => {
      this.ngZone.runOutsideAngular(() => {
        this.db.database.ref(environment.database.stream)
          .orderByChild('salesDate').startAt(Date.now() - 0 * 60 * 1000) // n分前からのデータを取得
          .on('child_added', snapshot => {
            const value: ReceiptWithKey = { $key: snapshot.key, ...snapshot.val() };
            this.ngZone.run(() => {
              observer.next(value);
            });
          });
      });
    });
  }

  get data$(): Observable<ReceiptWithKey> {
    return this._data$.filter(data => !!data);;
  }

  ngOnDestroy() {
    this.db.database.ref(environment.database.stream).off('child_added');
  }
}
