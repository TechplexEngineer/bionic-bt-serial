# bionic-bt-serial

Send data between bluetooth devices using a serial like interface

## Install

```bash
npm install bionic-bt-serial
npx cap sync
```

## API

<docgen-index>

* [`addListener(PluginEvents, ...)`](#addlistenerpluginevents)
* [`addListener('discoveryState', ...)`](#addlistenerdiscoverystate)
* [`addListener('discovered', ...)`](#addlistenerdiscovered)
* [`addListener('rawData', ...)`](#addlistenerrawdata)
* [`addListener('connected', ...)`](#addlistenerconnected)
* [`addListener('connectionFailed', ...)`](#addlistenerconnectionfailed)
* [`addListener('connectionLost', ...)`](#addlistenerconnectionlost)
* [`getBondedDevices()`](#getbondeddevices)
* [`startListening(...)`](#startlistening)
* [`stopListening()`](#stoplistening)
* [`isListening()`](#islistening)
* [`connect(...)`](#connect)
* [`isConnected(...)`](#isconnected)
* [`getConnectedDevices()`](#getconnecteddevices)
* [`disconnect(...)`](#disconnect)
* [`disconnectAll()`](#disconnectall)
* [`write(...)`](#write)
* [`isEnabled()`](#isenabled)
* [`enableAdapter()`](#enableadapter)
* [`disableAdapter()`](#disableadapter)
* [`showBluetoothSettings()`](#showbluetoothsettings)
* [`startDiscovery(...)`](#startdiscovery)
* [`cancelDiscovery()`](#canceldiscovery)
* [`setName(...)`](#setname)
* [`getName()`](#getname)
* [`setDiscoverable(...)`](#setdiscoverable)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### addListener(PluginEvents, ...)

```typescript
addListener(eventName: PluginEvents, callback: EventCallback) => Promise<PluginListenerHandle>
```

| Param           | Type                                                    |
| --------------- | ------------------------------------------------------- |
| **`eventName`** | <code><a href="#pluginevents">PluginEvents</a></code>   |
| **`callback`**  | <code><a href="#eventcallback">EventCallback</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('discoveryState', ...)

```typescript
addListener(eventName: 'discoveryState', callback: (result: { starting?: boolean; completed?: boolean; }) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                                           |
| --------------- | ------------------------------------------------------------------------------ |
| **`eventName`** | <code>'discoveryState'</code>                                                  |
| **`callback`**  | <code>(result: { starting?: boolean; completed?: boolean; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('discovered', ...)

```typescript
addListener(eventName: 'discovered', callback: (result: BTDevice) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                               |
| --------------- | ------------------------------------------------------------------ |
| **`eventName`** | <code>'discovered'</code>                                          |
| **`callback`**  | <code>(result: <a href="#btdevice">BTDevice</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('rawData', ...)

```typescript
addListener(eventName: 'rawData', callback: (result: { bytes: ArrayBufferLike; }) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                                                         |
| --------------- | -------------------------------------------------------------------------------------------- |
| **`eventName`** | <code>'rawData'</code>                                                                       |
| **`callback`**  | <code>(result: { bytes: <a href="#arraybufferlike">ArrayBufferLike</a>; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('connected', ...)

```typescript
addListener(eventName: 'connected', callback: (result: BTDevice) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                               |
| --------------- | ------------------------------------------------------------------ |
| **`eventName`** | <code>'connected'</code>                                           |
| **`callback`**  | <code>(result: <a href="#btdevice">BTDevice</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('connectionFailed', ...)

```typescript
addListener(eventName: 'connectionFailed', callback: (result: BTDevice) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                               |
| --------------- | ------------------------------------------------------------------ |
| **`eventName`** | <code>'connectionFailed'</code>                                    |
| **`callback`**  | <code>(result: <a href="#btdevice">BTDevice</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('connectionLost', ...)

```typescript
addListener(eventName: 'connectionLost', callback: (result: BTDevice) => void) => Promise<PluginListenerHandle>
```

| Param           | Type                                                               |
| --------------- | ------------------------------------------------------------------ |
| **`eventName`** | <code>'connectionLost'</code>                                      |
| **`callback`**  | <code>(result: <a href="#btdevice">BTDevice</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### getBondedDevices()

```typescript
getBondedDevices() => Promise<{ result: BTDevice[]; }>
```

Gets a list of the bonded (paired) devices.

**Returns:** <code>Promise&lt;{ result: BTDevice[]; }&gt;</code>

--------------------


### startListening(...)

```typescript
startListening(_options: {}, callback: ListenCallback) => Promise<void>
```

Start listening for incoming connections

| Param          | Type                                                      |
| -------------- | --------------------------------------------------------- |
| **`_options`** | <code>{}</code>                                           |
| **`callback`** | <code><a href="#listencallback">ListenCallback</a></code> |

--------------------


### stopListening()

```typescript
stopListening() => Promise<void>
```

Stops listening for incoming connections.

--------------------


### isListening()

```typescript
isListening() => Promise<{ result: boolean; }>
```

True if listening for an accepting incoming connections.
A device acting as a server should be listening.

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: { macAddress: string; }) => Promise<void>
```

Connect to another device acting as a server that is in listening mode.
Should already be paired?

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ macAddress: string; }</code> |

--------------------


### isConnected(...)

```typescript
isConnected(options: { macAddress: string; }) => Promise<{ result: boolean; }>
```

True if there is an active connection to the provided macAddress false otherwise.

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ macAddress: string; }</code> |

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### getConnectedDevices()

```typescript
getConnectedDevices() => Promise<{ result: BTDevice[]; }>
```

Gets a list of the connected devices.

**Returns:** <code>Promise&lt;{ result: BTDevice[]; }&gt;</code>

--------------------


### disconnect(...)

```typescript
disconnect(options: { macAddress: string; }) => Promise<{ result: boolean; }>
```

Disconnects specified connection.

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ macAddress: string; }</code> |

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### disconnectAll()

```typescript
disconnectAll() => Promise<void>
```

Disconnects all connections (incomming and outgoing).

--------------------


### write(...)

```typescript
write(options: { macAddress: string; data: ArrayBufferLike; }) => Promise<{ result: boolean; }>
```

Write data to specified macAddress

| Param         | Type                                                                                       |
| ------------- | ------------------------------------------------------------------------------------------ |
| **`options`** | <code>{ macAddress: string; data: <a href="#arraybufferlike">ArrayBufferLike</a>; }</code> |

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<{ result: boolean; }>
```

True if device has bluetooth enabled, false otherwise

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### enableAdapter()

```typescript
enableAdapter() => Promise<void>
```

Prompt the user to enable bluetooth.
Resolved if bluetooth is enabled, rejects otherwise.

--------------------


### disableAdapter()

```typescript
disableAdapter() => Promise<void>
```

Prompt the user to enable bluetooth.
Resolved if bluetooth is enabled, rejects otherwise.

--------------------


### showBluetoothSettings()

```typescript
showBluetoothSettings() => Promise<void>
```

Open the bluetooth settings screen for the user.

--------------------


### startDiscovery(...)

```typescript
startDiscovery(_options: {}, callback: DiscoveryCallback) => Promise<void>
```

Starts discovery process, sends info about found devices to the callback.
Scans for about 12 seconds.

| Param          | Type                                                            |
| -------------- | --------------------------------------------------------------- |
| **`_options`** | <code>{}</code>                                                 |
| **`callback`** | <code><a href="#discoverycallback">DiscoveryCallback</a></code> |

--------------------


### cancelDiscovery()

```typescript
cancelDiscovery() => Promise<void>
```

Stops any running discovery process.

--------------------


### setName(...)

```typescript
setName(options: { name: string; }) => Promise<void>
```

Sets the name of the bluetooth adapter. Name is what paired devices will see when they connect.

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ name: string; }</code> |

--------------------


### getName()

```typescript
getName() => Promise<{ result: string; }>
```

Gets the name of the bluetooth adapter.

**Returns:** <code>Promise&lt;{ result: string; }&gt;</code>

--------------------


### setDiscoverable(...)

```typescript
setDiscoverable(options: { durationSec?: number; }) => Promise<void>
```

Ensure bluetooth is enabled and the device is discoverable to remote scanners.
Default durationSec is 120 is not provided. Max is 300 seconds.

| Param         | Type                                   |
| ------------- | -------------------------------------- |
| **`options`** | <code>{ durationSec?: number; }</code> |

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ArrayBufferTypes

Allowed <a href="#arraybuffer">ArrayBuffer</a> types for the buffer of an ArrayBufferView and related Typed Arrays.

| Prop              | Type                                                |
| ----------------- | --------------------------------------------------- |
| **`ArrayBuffer`** | <code><a href="#arraybuffer">ArrayBuffer</a></code> |


#### ArrayBuffer

Represents a raw buffer of binary data, which is used to store data for the
different typed arrays. ArrayBuffers cannot be read from or written to directly,
but can be passed to a typed array or DataView Object to interpret the raw
buffer as needed.

| Prop             | Type                | Description                                                                     |
| ---------------- | ------------------- | ------------------------------------------------------------------------------- |
| **`byteLength`** | <code>number</code> | Read-only. The length of the <a href="#arraybuffer">ArrayBuffer</a> (in bytes). |

| Method    | Signature                                                                               | Description                                                     |
| --------- | --------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| **slice** | (begin: number, end?: number \| undefined) =&gt; <a href="#arraybuffer">ArrayBuffer</a> | Returns a section of an <a href="#arraybuffer">ArrayBuffer</a>. |


### Type Aliases


#### EventCallback

<code>(info: any, err?: any): void</code>


#### BTDevice

<code>{name:string, macAddress:string, class:number}</code>


#### ArrayBufferLike

<code>ArrayBufferTypes[keyof ArrayBufferTypes]</code>


#### ListenCallback

<code>(device: <a href="#btdevice">BTDevice</a>, err?: any): void</code>


#### DiscoveryCallback

<code>(device: <a href="#btdevice">BTDevice</a>, err?: any): void</code>


### Enums


#### PluginEvents

| Members                | Value                           |
| ---------------------- | ------------------------------- |
| **`discovered`**       | <code>"discovered"</code>       |
| **`discoveryState`**   | <code>"discoveryState"</code>   |
| **`rawData`**          | <code>"rawData"</code>          |
| **`connected`**        | <code>"connected"</code>        |
| **`connectionFailed`** | <code>"connectionFailed"</code> |
| **`connectionLost`**   | <code>"connectionLost"</code>   |

</docgen-api>
