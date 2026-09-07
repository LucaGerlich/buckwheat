# Payment capture (fork feature)

Automatic capture of card payments from bank notifications. This document covers the
first milestone: recording and replaying notifications. There is no bank parser, no
Actual Budget backend and no capture UI yet.

## Pipeline

```
Android notification
  → PaymentNotificationListenerService
  → NotificationExtractor
  → NotificationSnapshot
  → CaptureCoordinator
  → ParserRegistry (currently empty)
  → TransactionCandidate
```

`CaptureCoordinator` is the single entry point. Live notifications, replayed fixtures and
the debug payment simulator all use it, so debugging never exercises a different code path
than a real payment does.

Captured data intentionally does **not** go through `SpendsRepository`: candidates must not
touch Buckwheat's budget model before the user confirms them, so that Buckwheat and Actual
Budget never become two independent sources of truth for the same expense.

## Packages

| Path | Content |
| --- | --- |
| `notification/` | `NotificationSnapshot`, `NotificationExtractor`, `PaymentNotificationListenerService` |
| `capture/` | `CaptureCoordinator`, `CaptureRepository`, `CaptureViewModel`, `TransactionCandidate`, `CaptureStatus` |
| `capture/parser/` | `BankNotificationParser`, `ParserRegistry` |
| `capture/data/` | `NotificationFixture`, `NotificationFixtureDao` |
| `capture/debug/` | Notification inspector, fixture screen, payment simulator |

Fixtures are stored in the existing Room database (`buckwheat-db`, schema version 6) and
never leave the device.

## Manual test on a device

1. Enable the debug flag in Buckwheat, then open the debug menu (editor toolbar).
2. Open *Payment capture → Open notification inspector*.
3. Tap *Open notification access settings* and grant notification access to Buckwheat.
4. Trigger a notification (for example a real Trade Republic payment).
5. The notification appears in the inspector with its raw fields — tap *Save as fixture*.
6. Open *Payment capture → Open notification fixtures* and tap *Replay through pipeline*.
   The fixture goes through the regular pipeline; since no parser is registered yet, the
   snackbar reports that no parser recognised it.
7. *Payment capture → Open payment simulator* injects a manually entered payment as a
   `TransactionCandidate`, which allows developing the later capture UI without a parser.

## Next steps

The Trade Republic parser will be implemented only after a real notification has been
recorded, so that no assumptions about its text format are baked into the code.

## Tests

JVM unit tests live in `app/src/test/` and cover the extractor, the fixture roundtrip,
the parser registry and the coordinator:

```
./gradlew :app:testDebugUnitTest
```
