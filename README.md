# Forex Backend Ladder

## Test

To run the tests run the `sbt test` command.

**Note:**
Due to the short time available I wrote the essential tests to cover the happy path in the Http client and to test the process logics. With more time I could have written E2E tests. I tested the whole functionality in the local environment with Postman.

## Further improvements
I think the raw readers part that instantiates the app could be beyond simplified.
The error REST response could be in JSON format instead of plain text.
