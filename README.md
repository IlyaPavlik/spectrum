# spectrum
The app for color recognition. The app uses a camera for extracting colors from the picture. It can be one color (from center of the screen) or set of colors (palettes).

# Architecture

```mermaid
flowchart TB

subgraph Main[" "]
direction LR
  subgraph presentation["presentation"]
    View --> Presenter
    Presenter --> View
    Presenter --> Model
  end

  subgraph domain["domain"]
  direction LR
    interactor["interactor"]
  end

  subgraph data["data"]
  direction LR
    repository["repository"]
    database["database"]
  end

  presentation-->domain
  domain-->presentation
  domain-->data
  data-->domain
end
```



# Technologies
- Kotlin
- Coroutines
- Palette
- Glide
- Moxy
- Room
- Hilt
- Navigation component
- Camera2

