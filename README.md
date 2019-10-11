# Double Booked

The program takes in a sequence of events, each having a start and end time,
and returns to stdout the sequence of all pairs of overlapping events.

## Usage

### Input
  Input events should be defined as a pair of start-datetime and end-datetime separated by `,`:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z
  ```
  Events, in a sequence, should be separated by `;`:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z; 2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;
  ```

### Output
  Output is printed to stdout as pairs of overlapping events.
  Each pair is separated by newline.
  Each event in a pair is separated by `;`.
  Event's start-datetime and end-datetime are separated by `,`.

  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z
  ```

### Example
  Running:
  ```
  lein run '2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z; 2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z; 2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z; 2019-10-08T11:30:00.000Z, 2019-10-08T15:10:00.000Z'
  ```
  outputs:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z

  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z

  2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z

  2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z
  ```

## Tests
```
lein test
```
