import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import sdl.*

private const val SCREEN_WIDTH = 640
private const val SCREEN_HEIGHT = 480

fun main() {
    if (SDL_Init(SDL_INIT_EVERYTHING) != 0) {
        throw Error("${SDL_GetError()}")
    }

    val window = SDL_CreateWindow(
        "Hello from SDL + Kotlin!",
        SDL_WINDOWPOS_UNDEFINED.toInt(),
        SDL_WINDOWPOS_UNDEFINED.toInt(),
        SCREEN_WIDTH,
        SCREEN_HEIGHT,
        SDL_WINDOW_SHOWN
    ) ?: throw Error("${SDL_GetError()}")


    val renderer = SDL_CreateRenderer(
        window,
        -1,
        SDL_RENDERER_ACCELERATED
    ) ?: throw Error("${SDL_GetError()}")

    var quit = false

    memScoped {
        val rect = alloc<SDL_Rect> {
            w = SCREEN_WIDTH / 4
            h = w
            x = (SCREEN_WIDTH / 2) - (w / 2)
            y = (SCREEN_HEIGHT / 2) - (h / 2)
        }

        while (!quit) {
            println("${rect.x}, ${rect.y}, ${rect.w}, ${rect.h}")
            SDL_SetRenderDrawColor(renderer, 0, 0, 0, 0)
            SDL_RenderClear(renderer)

            val event = alloc<SDL_Event>()

            while (SDL_PollEvent(event.ptr) != 0) {
                if (event.type == SDL_QUIT) {
                    quit = true
                    break
                }

                if (event.type == SDL_KEYDOWN) {
                    when (event.key.keysym.sym.toUInt()) {
                        SDLK_UP -> rect.y = (rect.y - 20).coerceIn(0, SCREEN_HEIGHT)
                        SDLK_DOWN -> rect.y = (rect.y + 20).coerceIn(0, SCREEN_HEIGHT - rect.h)
                        SDLK_LEFT -> rect.x = (rect.x - 20).coerceIn(0, SCREEN_WIDTH)
                        SDLK_RIGHT -> rect.x = (rect.x + 20).coerceIn(0, SCREEN_WIDTH - rect.w)
                    }
                }
            }
            SDL_SetRenderDrawColor(renderer, 0xFF, 0xFF, 0xFF, 0xFF)
            SDL_RenderFillRect(renderer, rect.ptr)
            SDL_RenderPresent(renderer)
        }
    }

    SDL_DestroyWindow(window)
    SDL_Quit()
}
