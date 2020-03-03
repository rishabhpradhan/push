import { Component } from '@angular/core';
import { UserMessage } from  './user-message';
import { HttpClient } from "@angular/common/http";
//import { EventSourcePolyfill } from 'ng-event-source';
import { Injectable } from '@angular/core';
import { environment } from './../environments/environment';
//import * as EventSource from 'eventsource';
//import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

@Injectable()
export class AppComponent {
  title = 'Wave';
  subscriber : string = '';
  submitted : boolean = false;
  messages : Array<UserMessage> = [];

  source : EventSource = null;

  constructor(private http: HttpClient) { }

  subscribe() {
    this.submitted = true;
    this.connect();
  }

  unsubscribe() {
     this.http.delete(environment.baseUrl + '/notification/unsubscribe/' +this.subscriber).subscribe();
     this.submitted = false;
     this.subscriber = '';
     this.messages = new Array<UserMessage>();
     this.source.close();
  }

  connect(): void {
    this.source = new EventSource(environment.baseUrl + '/notification/subscribe/' +this.subscriber);
    //let eventSource = new NativeEventSource('http://localhost:8081/notification/subscribe/' +this.subscriber);
    //let source  = new EventSourcePolyfill('http://localhost:8081/notification/subscribe/' +this.subscriber, { heartbeatTimeout: 1000, connectionTimeout: 1000 });
    //let source = new EventSourcePolyfill('http://localhost:8081/notification/subscribe/' +this.subscriber, {headers: { headerName: 'HeaderValue', header2: 'HeaderValue2' }});

    this.source.onmessage = (message => {
       console.log(message);
    });

    this.source.onerror = ( event => {
       console.log('Error occured' + event);
    });


    this.source.addEventListener('push',  message => {
      this.messages.push(JSON.parse((<any>message).data));
    });
 }
}
