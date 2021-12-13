# bionic-bt-serial

Send data between bluetooth devices using a serial like interface

## Install

```bash
npm install bionic-bt-serial
npx cap sync
```

## API

<docgen-index>

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
* [`enable()`](#enable)
* [`showBluetoothSettings()`](#showbluetoothsettings)
* [`startDiscovery(...)`](#startdiscovery)
* [`cancelDiscovery()`](#canceldiscovery)
* [`setName(...)`](#setname)
* [`getName()`](#getname)
* [`setDiscoverable(...)`](#setdiscoverable)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getBondedDevices()

```typescript
getBondedDevices() => Promise<{ devices: BTDevice[]; }>
```

Gets a list of the bonded (paired) devices.

**Returns:** <code>Promise&lt;{ devices: BTDevice[]; }&gt;</code>

--------------------


### startListening(...)

```typescript
startListening(callback: ListenCallback) => Promise<void>
```

Start listening for incomming connections

| Param          | Type                                                      |
| -------------- | --------------------------------------------------------- |
| **`callback`** | <code><a href="#listencallback">ListenCallback</a></code> |

--------------------


### stopListening()

```typescript
stopListening() => Promise<void>
```

Stops listening for incomming connections.

--------------------


### isListening()

```typescript
isListening() => Promise<{ result: boolean; }>
```

True if listening for an accepting incomming connections.
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
getConnectedDevices() => Promise<{ devices: string[]; }>
```

Gets a list of the connected devices.

**Returns:** <code>Promise&lt;{ devices: string[]; }&gt;</code>

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
write(options: { macAddress: string; data: object; }) => Promise<{ result: boolean; }>
```

Write data to specified macAddress

| Param         | Type                                               | Description |
| ------------- | -------------------------------------------------- | ----------- |
| **`options`** | <code>{ macAddress: string; data: object; }</code> | The options |

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<{ result: boolean; }>
```

True if device has bluetooth enabled, false otherwise

**Returns:** <code>Promise&lt;{ result: boolean; }&gt;</code>

--------------------


### enable()

```typescript
enable() => Promise<void>
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
startDiscovery(callback: DiscoveryCallback) => Promise<void>
```

Starts discovery process, sends info about found devices to the callback.
Scans for about 12 seconds.

| Param          | Type                                                            |
| -------------- | --------------------------------------------------------------- |
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

Sets the nameov the bluetooth adapter. Name is what paired devices will see when they connect.

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ name: string; }</code> |

--------------------


### getName()

```typescript
getName() => Promise<{ name: string; }>
```

Gets the name of the bluetooth adapter.

**Returns:** <code>Promise&lt;{ name: string; }&gt;</code>

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


### Type Aliases


#### BTDevice

<code>{name:string, macAddress:string, id:string, class:number}</code>


#### ListenCallback

<code>(message: { connected: <a href="#btdevice">BTDevice</a>; }, err?: any): void</code>


#### DiscoveryCallback

<code>(message: { device: <a href="#btdevice">BTDevice</a>; }, err?: any): void</code>

</docgen-api>
