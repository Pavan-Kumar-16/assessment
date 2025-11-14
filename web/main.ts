/**
 * main.ts - TypeScript viewer for FLMap
 * Displays static base64 image with FPS and resolution overlay
 */

// Sample base64 image (1x1 transparent pixel as placeholder)
const PLACEHOLDER_IMAGE = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';

// Display elements
const displayImage = document.getElementById('displayImage') as HTMLImageElement;
const fpsValue = document.getElementById('fpsValue') as HTMLSpanElement;
const resolutionValue = document.getElementById('resolutionValue') as HTMLSpanElement;

// FPS tracking
let frameCount = 0;
let lastTime = performance.now();
let currentFPS = 0;

/**
 * Initialize the viewer
 */
function init(): void {
    // Set placeholder image
    displayImage.src = PLACEHOLDER_IMAGE;
    
    // Update resolution display
    updateResolution(0, 0);
    
    // Start FPS counter
    startFPSCounter();
    
    console.log('FLMap Viewer initialized');
}

/**
 * Update FPS display
 */
function updateFPS(fps: number): void {
    currentFPS = fps;
    fpsValue.textContent = fps.toFixed(1);
}

/**
 * Update resolution display
 */
function updateResolution(width: number, height: number): void {
    resolutionValue.textContent = `${width}x${height}`;
}

/**
 * Update displayed image from base64 data
 */
function updateImage(base64Data: string, width: number, height: number): void {
    displayImage.src = base64Data;
    updateResolution(width, height);
    frameCount++;
}

/**
 * Start FPS counter loop
 */
function startFPSCounter(): void {
    function calculateFPS(): void {
        const currentTime = performance.now();
        const deltaTime = currentTime - lastTime;
        
        if (deltaTime >= 1000) {
            const fps = (frameCount * 1000) / deltaTime;
            updateFPS(fps);
            frameCount = 0;
            lastTime = currentTime;
        }
        
        requestAnimationFrame(calculateFPS);
    }
    
    requestAnimationFrame(calculateFPS);
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
} else {
    init();
}

// Export functions for potential external use
(window as any).updateImage = updateImage;
(window as any).updateFPS = updateFPS;
(window as any).updateResolution = updateResolution;


