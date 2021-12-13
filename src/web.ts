import { WebPlugin } from '@capacitor/core';

import type {
  BluetoothSerialPlugin,
  ListenCallback,
  DiscoveryCallback,
  BTDevice
} from './definitions';

function getRandomInt(min:number, max:number): number {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min) + min); //The maximum is exclusive and the minimum is inclusive
}

function getMockDevice(): BTDevice {
  return {
    "name":`DeviceName ${getRandomInt(1, 10)}`,
    "macAddress":"AA:BB:CC:DD:EE:FF",
    "id":"AA:BB:CC:DD:EE:FF",
    "class":524
  };
}

export class BluetoothSerialWeb
  extends WebPlugin
  implements BluetoothSerialPlugin {

    discoveryHandle: ReturnType<typeof setInterval>|null = null;
    listeningHandle: ReturnType<typeof setInterval>|null = null;
    adapterName = "default adapter name";
    mIsListening = false;
    connections: {[key: string]: BTDevice} = {};

    async getBondedDevices(): Promise<{ devices: BTDevice[] }> {
      return new Promise((resolve, _)=>{
        resolve({devices: [getMockDevice(), getMockDevice()]})
      });
    }

    async startListening(callback: ListenCallback): Promise<void> {
      this.mIsListening = true;
      this.listeningHandle = setInterval(()=>{
        let dev = getMockDevice()
        this.connections[dev.macAddress] = dev;
        callback({connected: dev})
      }, 2000)

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async stopListening(): Promise<void> {
      this.mIsListening = false;
      if (this.listeningHandle != null) {
        clearInterval(this.listeningHandle);
        this.listeningHandle = null;
      }
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isListening(): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: this.mIsListening})
      })
    }

    async connect(options: { macAddress:string }): Promise<void> {
      this.connections[options.macAddress] = {
        name: options.macAddress,
        macAddress: options.macAddress,
        id: options.macAddress,
        "class": 524
      };
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async isConnected(_options: { macAddress:string }): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true});
      })
    }

    async getConnectedDevices(): Promise<{ devices: string[] }> {
      return new Promise((resolve,_)=>{
        resolve({devices: Object.keys(this.connections)});
      })
    }

    async disconnect(_options: { macAddress:string }): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async disconnectAll(): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async write(_options: {macAddress:string, data:object}): Promise<{ result: boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async isEnabled(): Promise<{result:boolean}> {
      return new Promise((resolve,_)=>{
        resolve({result: true})
      })
    }

    async enable(): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async showBluetoothSettings(): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async startDiscovery(callback: DiscoveryCallback): Promise<void> {
      this.discoveryHandle = setInterval(()=>{
        callback({device: getMockDevice()})
      }, 2000); // get a mock device every 2 seconds
      setTimeout(this.cancelDiscovery, 12*1000); // only discover for 12 seconds

      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async cancelDiscovery(): Promise<void> {
      return new Promise((resolve,_)=>{
        if (this.discoveryHandle != null) {
          clearInterval(this.discoveryHandle);
          this.discoveryHandle = null;
        }
        resolve()
      })
    }

    async setName(options: {name: string}): Promise<void> {
      this.adapterName = options.name;
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

    async getName(): Promise<{name: string}> {
      return new Promise((resolve,_)=>{
        resolve({name: this.adapterName})
      })
    }

    async setDiscoverable(_options: {durationSec?: number}): Promise<void> {
      return new Promise((resolve,_)=>{
        resolve()
      })
    }

}


