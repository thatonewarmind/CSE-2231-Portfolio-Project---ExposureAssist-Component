> Q; Why is a class like CapturingWriter needed?

> A: That class is implemented specifically for testing of the displayBalanceAdvice method. The issue I ran into when writing code is that I realized a big issue.
> JUnit isn't really able to test displayBalanceAdvice because the method outputs directly to the terminal; its end result isn't stored anywhere before being shot to the terminal
> CapturingWriter is made so that when the method runs and we try to test it, we are able to capture the balance advice in a string and run tests on that String variable

> Q: What are test values based off of?

> A: For numerical values, they come from my research and experience in photography. As an example, aperture ranging from f/0.5 to f/64 is set like that because there are(rarely) people who use lenses that open up that wide and others who use lenses that are that tightly shut. Even though neither of these boundary apertures make sense to use in 99% of photography, I thought that making this component as versatile as possible at first was a better idea.

> For text values, I'm just testing them to see if outputs are giving proper advice(displayBalanceAdvice is a good example where I'm looking for the method to output specific keywords like increase and decrease depending on the user's current EV value)
