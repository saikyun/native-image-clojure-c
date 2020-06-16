#include <stdio.h>
#include <SDL2/SDL.h>

//Screen dimension constants
const int SCREEN_WIDTH = 640;
const int SCREEN_HEIGHT = 480;

int get_sdl_quit() { return SDL_QUIT; }

int _SDL_PollEvent(SDL_Event* event) {
  return SDL_PollEvent(event);
}

SDL_Event e;

SDL_Event *get_e() {
  return &e;
}

int quit = 0;


//The window we'll be rendering to
SDL_Window* window = NULL;


int beginning() {
  
  //The surface contained by the window
  SDL_Surface* screenSurface = NULL;


  //Initialize SDL
  if( SDL_Init( SDL_INIT_VIDEO ) < 0 )
    {
      printf( "SDL could not initialize! SDL_Error: %s\n", SDL_GetError() );
      return 0;
    }
  
  //Create window
  window = SDL_CreateWindow( "SDL Tutorial", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, SDL_WINDOW_SHOWN );
  if( window == NULL )
    {
      printf( "Window could not be created! SDL_Error: %s\n", SDL_GetError() );
      return 0;
    }
  
  //Get window surface
  screenSurface = SDL_GetWindowSurface( window );
  
  //Fill the surface white
  SDL_FillRect( screenSurface, NULL, SDL_MapRGB( screenSurface->format, 0xFF, 0xFF, 0xFF ) );
  
  SDL_Rect rect = {0, 0, 100, 50}; // the rectangle
  SDL_FillRect( screenSurface, &rect, SDL_MapRGB(screenSurface->format, 255, 0, 0) );
                
  //Update the surface
  SDL_UpdateWindowSurface( window );

  return 0;
}


void middle() {
  while (quit == 0) {                                         
    while (_SDL_PollEvent(get_e())) {
      if (get_e()->type == SDL_QUIT) {
        quit = 1;
      }
    }
  }
}

void end() {
  //Destroy window
SDL_DestroyWindow( window );
 
  SDL_Quit();
}
